package com.middleware.request;

import com.middleware.config.ConfigClient;
import com.middleware.frame.common.BaseThread;

import java.io.IOException;

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
/*
        if (mTcpClient == null)
        {
            try {
                mTcpClient = new RequestTcpClient("192.168.1.102",60002);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
//*/
        return  false;
    }
}
