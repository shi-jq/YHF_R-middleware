package com.middleware.request;

import com.middleware.config.ConfigMngr;
import com.middleware.config.ConfigPcSerial;
import com.middleware.frame.common.BaseThread;
import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.PrintCtrl;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.RFrameList;
import com.middleware.frame.main.FrameMsgObervable;
import com.middleware.frame.main.MsgMngr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import cn.pda.serialport.SerialPort;


/**
 * 串口连接的请求者
 */
public class RequestSerial extends BaseThread implements Observer {

    private FrameMsgObervable toAndroid = MsgMngr.PcToAndroidMsgObv;
    private DataProc mProc = new DataProc();
    private RFrameList mRFrameList = new RFrameList();
    private SerialPort mSerialPort = null;
    ConfigPcSerial config  = null;
    //是否需要断线重连
    private boolean mIsNeedAddTimeTagFrame = false;
    private boolean mAutoSendHeath = false;//自动发送心跳

    String path = new String("/dev/ttyS0");

    public RequestSerial() throws IOException {
        super("RequestSerial",true);

        config = ConfigMngr.pcSerial;

        try {
            this.mSerialPort = new SerialPort(new File(path), config.baudrate, 0);
        } catch (IOException e) {
            this.mSerialPort = null;
            throw  e;
        }

        MsgMngr.AndroidToPcTagObv.addObserver(this);
    }

    public void setNeedTimeTagFrame(boolean need)
    {
        mIsNeedAddTimeTagFrame = need;
    }

    public boolean Quit()
    {
        try {
            mSerialPort.getInputStream().close();
            mSerialPort.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSerialPort.close();

        this.stop();
        return true;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        //如果已经断开了, 就把数据丢了
        if (mSerialPort == null)
        {
            return;
        }

        if ( MsgMngr.AndroidToPcTagObv == o)
        {
            RFrame frame = (RFrame) arg;
            assert  (frame != null);
            OutputStream ouputStream =  mSerialPort.getOutputStream();
            assert(ouputStream != null);
            try {

                byte[] pForSend = new byte[DataProc.SEND_FRAME_MAXBUFF];
                INT32U totalFrameSize = new INT32U(DataProc.SEND_FRAME_MAXBUFF);

                mProc.PackMsg(pForSend,totalFrameSize, frame);

                ouputStream.write(pForSend,0,totalFrameSize.GetValue());

                PrintCtrl.PrintBUffer("标签数据发送到PC ", pForSend, totalFrameSize.GetValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void sendResultToPc(RFIDFrame rfidFrame)
    {
        assert  (rfidFrame != null);
        OutputStream ouputStream =  mSerialPort.getOutputStream();
        assert(ouputStream != null);
        try {
            RFrame recvFrame = rfidFrame.GetRevFrame();
            if (recvFrame == null)
            {
                //有可能没有应答, 没有应答则不做处理,因为本身已经超时了
                return;
            }

            byte[] pForSend = new byte[DataProc.SEND_FRAME_MAXBUFF];
            INT32U totalFrameSize = new INT32U(DataProc.SEND_FRAME_MAXBUFF);

            mProc.PackMsg(pForSend,totalFrameSize, recvFrame);
            ouputStream.write(pForSend,0,totalFrameSize.GetValue());

            PrintCtrl.PrintBUffer("数据发送到PC ", pForSend, totalFrameSize.GetValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int bsize = 0;
    private byte[] buffer = new byte[DataProc.SEND_FRAME_MAXBUFF];
    @Override
    public boolean threadProcess()
    {
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

        PrintCtrl.PrintBUffer("数据从PC读取 ", buffer, bsize);
        if (bsize > 0)
        {
            mProc.UnPackMsg(buffer,bsize);
            mProc.GetFrameList(mRFrameList);
            for (int i = 0; i < mRFrameList.GetCount(); i++) {
                RFrame pRFrame = mRFrameList.GetRFrame(i);
                RFIDFrame rfidFrame = new RFIDFrame(pRFrame);
                toAndroid.dealFrame(rfidFrame);
                sendResultToPc(rfidFrame);
            }
            mRFrameList.ClearAll();
            return  true;
        }

        return  false;
    }
}
