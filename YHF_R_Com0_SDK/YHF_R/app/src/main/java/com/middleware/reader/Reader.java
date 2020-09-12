package com.middleware.reader;

import com.middleware.config.ConfigMngr;
import com.middleware.config.ConfigReaderSerial;
import com.middleware.frame.common.BaseThread;
import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.PrintCtrl;
import com.middleware.frame.common.RftserW32;
import com.middleware.frame.ctrl.ReaderState;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.RFrameList;
import com.middleware.frame.main.FrameTagObervable;
import com.middleware.frame.main.MsgMngr;
import com.middleware.request.RequestMngr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import cn.pda.serialport.SerialPort;


public class Reader extends BaseThread implements Observer   {

    private  ReaderState readerStatus = ReaderState.READER_STATE_INIT;

    private DataProc mProc = new DataProc();
    private RFrameList mRecvRFrameList = new RFrameList();
    private SerialPort mSerialPort = null;
    ConfigReaderSerial config  = ConfigMngr.getInstance().readerSerial;
    FrameTagObervable toAndroidTag = MsgMngr.ModelToAndroidTagObv;

    String path = new String("/dev/ttyS1");

    public Reader() throws IOException {
        super("Reader",true);

        try {
            //读写器模块启动需要先上电

            File file = new File(path);
            this.mSerialPort = new SerialPort(file,config.baudrate, 0);

        } catch (IOException e) {

            this.mSerialPort = null;
            this.isNeedStop = true;
            this.resume();
            throw  e;
        }

        MsgMngr.AndroidToModelMsgObv.addObserver(this);
        this.resume();
    }

    @Override
    public void onDestory() {
        mSerialPort.close();
        super.onDestory();
    }

    //普通命令是
    @Override
    public void update(Observable o, Object arg) {
        RFIDFrame rfidFrame = (RFIDFrame) arg;
        assert  (rfidFrame != null);
        OutputStream ouputStream =  mSerialPort.getOutputStream();
        assert(ouputStream != null);
        try {

            byte[] pForSend = new byte[DataProc.SEND_FRAME_MAXBUFF];
            INT32U totalFrameSize = new INT32U(DataProc.SEND_FRAME_MAXBUFF);
            RFrame sendFrame =  rfidFrame.GetSendFrame();
            mProc.PackMsg(pForSend,totalFrameSize, sendFrame);
            ouputStream.write(pForSend,0,totalFrameSize.GetValue());
            PrintCtrl.PrintBUffer("数据发送到模块 ", pForSend, totalFrameSize.GetValue());

            if (mProc.isStartRead(sendFrame.GetRfidCommand()))
            {
            }
            else{
                //会等待超时
                RFrame pRFrame =  waitRecvFrame(rfidFrame.GetSendFrame(),3000);
                rfidFrame.AddRevFrame(pRFrame);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    RFrame waitRecvFrame(RFrame sendFrame, int timeout)
    {
        byte btRfidCommand = sendFrame.GetRfidCommand();
        RFrame pRFrame = null;
        long cur_ms = RftserW32.Ser32_TickCount();
        while (!RftserW32.Ser_CheckTimeout(cur_ms, timeout)) {

            pRFrame = mRecvRFrameList.GetRFrame(btRfidCommand,
                    sendFrame.GetBusAddr(),true);
            if (pRFrame != null)
            {
                return pRFrame;
            }
        }

        return null;
    }

    private RFrameList temList = new RFrameList();
    private int bsize = 0;
    private byte[] buffer = new byte[DataProc.SEND_FRAME_MAXBUFF];

    @Override
    public boolean threadProcess() {

        if (mSerialPort == null)
        {
            return false;
        }

        InputStream inputStream =  mSerialPort.getInputStream();
        assert(inputStream != null);
        bsize = 0;
        try {

            bsize = inputStream.read(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintCtrl.PrintBUffer("数据从模块读取 ", buffer, bsize);
        if (bsize > 0)
        {
            mProc.UnPackMsg(buffer,bsize);
            mProc.GetFrameList(temList);
            for (int i = 0; i < temList.GetCount(); i++) {
                RFrame pRFrame = temList.GetRFrame(i);
                byte byteCommand = pRFrame.GetRfidCommand();
                if (mProc.isReportCommand(byteCommand))
                {
                    //pRFrame.SetByte(6, (byte) 0x01);
                    //mProc.ResetFrameCrc(pRFrame);
                    toAndroidTag.sendTag(pRFrame);
                    RequestMngr.getInstance().sendToPC(pRFrame);
                }
                else{
                    mRecvRFrameList.AddRFrame(pRFrame);
                }
            }
            temList.ClearAll();
            return  true;
        }

        return false;
    }
}
