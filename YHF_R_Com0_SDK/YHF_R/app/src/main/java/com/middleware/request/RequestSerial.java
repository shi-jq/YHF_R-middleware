package com.middleware.request;

import com.middleware.config.ConfigMngr;
import com.middleware.connect.ConnectBase;
import com.middleware.connect.ConnectSerial;
import com.middleware.connect.SerialConfig;
import com.middleware.frame.common.BaseThread;
import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.PrintCtrl;
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


public class RequestSerial extends BaseThread implements Observer {

    private FrameMsgObervable toAndroid = null;
    private DataProc mProc = new DataProc();
    private RFrameList mRFrameList = new RFrameList();
    private ConnectBase mConnect = null;

    //是否需要断线重连
    private boolean mIsNeedAddTimeTagFrame = false;
    private boolean mAutoSendHeath = false;//自动发送心跳

    public RequestSerial()
    {
        super("RequestSerial",true);

        SerialConfig config  = new SerialConfig();
        config.baudrate = ConfigMngr.pcSerial.baudrate;
        config.comNum = ConfigMngr.pcSerial.comNum;
        mConnect = new ConnectSerial(config);

        MsgMngr.AndroidToPcTagObv.addObserver(this);
        toAndroid = MsgMngr.PcToAndroidMsgObv;
    }

    public void setNeedTimeTagFrame(boolean need)
    {
        mIsNeedAddTimeTagFrame = need;
    }

    public boolean isColsed()
    {
        //连接已经关闭,且不需要重连的,则需要退出
        if (mConnect.isColsed())
        {
            return true;
        }
        return false;
    }

    public boolean reconnect()
    {
        if (mConnect != null)
        {
            mConnect.reconnect();
        }

        return true;
    }

    public boolean Quit()
    {
        if (mConnect != null)
        {
            mConnect.close();
            mConnect.quit();
            mConnect = null;
        }

        this.stop();
        return true;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        //如果已经断开了, 就把数据丢了
        if (mConnect == null)
        {
            return;
        }

        if ( mConnect.isColsed())
        {
            return;
        }

        if ( MsgMngr.AndroidToPcTagObv == o)
        {
            RFrame frame = (RFrame) arg;
            assert  (frame != null);
            OutputStream ouputStream =  mConnect.getOutputStream();
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
        OutputStream ouputStream =  mConnect.getOutputStream();
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
        if (mConnect == null)
        {
            return false;
        }

        if (mConnect.isColsed())
        {
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
