package com.middleware.reader;

import com.middleware.connect.ConnectBase;
import com.middleware.frame.common.BaseThread;
import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.PrintCtrl;
import com.middleware.frame.common.RftserW32;
import com.middleware.frame.ctrl.ReaderState;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.RFrameList;
import com.middleware.frame.main.FrameMsgObervable;
import com.middleware.frame.main.MsgMngr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

public class Reader extends BaseThread implements Observer   {

    private  ReaderState readerStatus = ReaderState.READER_STATE_INIT;

    private DataProc mProc = new DataProc();
    private RFrameList mRecvRFrameList = new RFrameList();
    private ConnectBase mConnect = null;

    FrameMsgObervable toAndroid = null;
    Reader(ConnectBase connect)
    {
        super("Reader",true);
        mConnect = connect;
        MsgMngr.AndroidToModelObv.addObserver(this);
        toAndroid = MsgMngr.ModelToAndroidObv;

    }

    @Override
    public void update(Observable o, Object arg) {
        RFIDFrame rfidFrame = (RFIDFrame) arg;
        assert  (rfidFrame != null);
        OutputStream ouputStream =  mConnect.getOutputStream();
        assert(ouputStream != null);
        try {

            byte[] pForSend = new byte[1024];
            INT32U totalFrameSize = new INT32U(1024);

            mProc.PackMsg(pForSend,totalFrameSize, rfidFrame.GetSendFrame());
            ouputStream.write(pForSend,0,totalFrameSize.GetValue());
            PrintCtrl.PrintBUffer("数据发送到模块 ", pForSend, totalFrameSize.GetValue());

            RFrame pRFrame =  waitRecvFrame(rfidFrame.GetSendFrame(),3000);
            rfidFrame.AddRevFrame(pRFrame);

            toAndroid.sendFrame(rfidFrame);

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

            pRFrame = mRecvRFrameList.GetRFrame(btRfidCommand,true);
            if (pRFrame != null) {
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

        if (mConnect == null)
        {
            return false;
        }

        if (mConnect.isColsed())
        {
            mConnect.reconnect();
            return false;
        }

        InputStream inputStream =  mConnect.getInputStream();
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
            mProc.GetFrameList(temList);
            for (int i = 0; i < temList.GetCount(); i++) {
                RFrame pRFrame = temList.GetRFrame(i);
                mRecvRFrameList.AddRFrame(pRFrame);
            }
            temList.ClearAll();
            return  true;
        }

        return false;
    }
}
