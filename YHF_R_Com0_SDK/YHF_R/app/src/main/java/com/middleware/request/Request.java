package com.middleware.request;

import com.middleware.connect.ConnectBase;
import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.PrintCtrl;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.RFrameList;
import com.middleware.frame.main.FrameMngr;
import com.middleware.frame.main.FrameMsgObervable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

public class Request implements Observer {

    FrameMsgObervable toAndroid = null;
    MyRunnable myRunnable = null;
    private Thread mThead = null;

    private RFrameList mRFrameList = new RFrameList();
    Request(FrameMngr framMngr)
    {
        framMngr.AndroidToPcObv.addObserver(this);
        toAndroid = framMngr.PcToAndroidObv;

        myRunnable = new MyRunnable();
        mThead = new Thread(myRunnable);
        mThead.start();
    }

    private ConnectBase connect = null;
    public boolean SendFrame(RFrame frame)
    {
        return  false;
    }

    public RFrame ReacvFrame()
    {
        return  null;
    }

    @Override
    public void update(Observable o, Object arg) {
        RFIDFrame rfidFrame = (RFIDFrame) arg;
        assert  (rfidFrame != null);
        OutputStream ouputStream =  connect.getOutputStream();
        try {
            DataProc proc = new DataProc();
            byte[] pForSend = new byte[1024];
            INT32U totalFrameSize = new INT32U(1024);

            //proc.PackMsg(pRfidCommand, pSendData, dataLength, pForSend,
           //         totalFrameSize, pRfidFrame.GetSendFrame());

            ouputStream.write(rfidFrame.GetRevFrame().GetData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class MyRunnable
            implements Runnable {
        public void run() {

            InputStream inputStream =  connect.getInputStream();
            int bsize = 0;
            byte[] buffer = new byte[1024];
            DataProc proc = new DataProc();


            while (true)
            {
                bsize = 0;
                try {
                    bsize = inputStream.read(buffer);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                PrintCtrl.PrintBUffer("数据读取 ", buffer, bsize);
                if (bsize > 0)
                {
                    proc.UnPackMsg(buffer,bsize);
                    proc.GetFrameList(mRFrameList);
                    for (int i = 0; i < mRFrameList.GetCount(); i++) {
                        RFrame pRFrame = mRFrameList.GetRFrame(i);
                        RFIDFrame rfidFrame = new RFIDFrame(pRFrame);
                        toAndroid.sendFrame(rfidFrame);
                    }
                    mRFrameList.ClearAll();
                }

                try {
                    Thread.sleep(500L);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }
}
