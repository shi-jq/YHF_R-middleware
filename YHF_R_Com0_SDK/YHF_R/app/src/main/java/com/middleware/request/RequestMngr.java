package com.middleware.request;

import com.middleware.config.ConfigClient;
import com.middleware.config.ConfigMngr;
import com.middleware.frame.common.BaseThread;

import java.io.IOException;
import java.util.Iterator;

public class RequestMngr extends BaseThread {
    //通过串口连接的
    private RequestSerial mSerialRequest = null;
    private RequestTcpServer mTcpServer = null;
    private RequestTcpClient mTcpClient = null;


    public RequestMngr()
    {
        super("RequestMngr",false);

        recreateSerialRequest();

    }

    //重新创建串口, 串口波特率改变的时候 需要去调整
    private  void recreateSerialRequest()
    {

    }


    public void createOneTcpClint(ConfigClient configF)
    {

    }


    @Override
    public boolean threadProcess()
    {
///*
        if (mTcpServer == null)
        {
            try {
                int lisenPort  = 60001;
                mTcpServer = new RequestTcpServer(60001);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mTcpClient == null)
        {
            mTcpClient = new RequestTcpClient("127.0.0.1",60001);
        }
//*/
        return  false;
    }
}
