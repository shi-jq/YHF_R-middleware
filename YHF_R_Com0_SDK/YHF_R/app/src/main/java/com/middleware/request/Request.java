package com.middleware.request;

import com.middleware.connect.ConnectBase;
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

public class Request extends BaseThread implements Observer {

    private FrameMsgObervable toAndroid = null;
    private DataProc mProc = new DataProc();
    private RFrameList mRFrameList = new RFrameList();
    private ConnectBase mConnect = null;

    //是否需要断线重连
    private boolean mIsNeedReconnect = false;
    private boolean mIsNeedAddTimeTagFrame = false;

    Request(ConnectBase connect,String name,boolean needReconnect)
    {
        super(name,true);
        mConnect = connect;
        mIsNeedReconnect = needReconnect;

        MsgMngr.AndroidToPcObv.addObserver(this);
        toAndroid = MsgMngr.PcToAndroidObv;
    }

    public void setNeedTimeTagFrame(boolean need)
    {
        mIsNeedAddTimeTagFrame = need;
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

        RFIDFrame rfidFrame = (RFIDFrame) arg;
        assert  (rfidFrame != null);
        OutputStream ouputStream =  mConnect.getOutputStream();
        assert(ouputStream != null);
        try {

            byte[] pForSend = new byte[1024];
            INT32U totalFrameSize = new INT32U(1024);

            mProc.PackMsg(pForSend,totalFrameSize, rfidFrame.GetRevFrame());

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

        //如果链接已经关闭了, 且需要重连,则重连
        if (mConnect.isColsed() &&  mIsNeedReconnect)
        {
            mConnect.reconnect();
            //重连后,下一个时间片处理
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
                toAndroid.sendFrame(rfidFrame);
            }
            mRFrameList.ClearAll();
            return  true;
        }

        return  false;
    }
}
