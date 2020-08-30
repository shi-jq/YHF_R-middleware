package com.middleware.request;

import com.middleware.config.ConfigClient;
import com.middleware.config.ConfigMngr;
import com.middleware.connect.ConnectSerial;
import com.middleware.connect.ConnectTcpClient;
import com.middleware.connect.SerialConfig;
import com.middleware.connect.TcpClientConfig;
import com.middleware.frame.common.BaseThread;

import java.util.Iterator;
import java.util.LinkedList;

public class RequestMngr extends BaseThread {
    //通过串口连接的
    private Request mSerialRequest = null;
    //通过tcp udp客户端连接的
    private LinkedList<Request> mClientRequestList = new LinkedList<Request>();
    //通过tcpserver udpserver 连接进来的
    private LinkedList<Request> mServerRequestList = new LinkedList<Request>();

    public RequestMngr()
    {
        super("RequestMngr",true);

        recreateSerialRequest();
    }

    //重新创建串口, 串口波特率改变的时候 需要去调整
    private  void recreateSerialRequest()
    {
        if (mSerialRequest != null)
        {
            mSerialRequest.Quit();
            mSerialRequest = null;
        }

        SerialConfig config  = new SerialConfig();
        config.baudrate = ConfigMngr.pcSerial.baudrate;
        config.comNum = ConfigMngr.pcSerial.comNum;
        ConnectSerial connect = new ConnectSerial(config);
        mSerialRequest = new Request(connect);
    }

    //重新设定转发的客户端
    private void recreatAllClient()
    {
        clearAllClient();
        createAllClient();
    }

    private void clearAllClient()
    {
        Iterator<Request> it = mClientRequestList.iterator();
        while(it.hasNext()){
            Request request = it.next();
            request.Quit();
        }
        mClientRequestList.clear();
    }

    private void createAllClient()
    {
        Iterator<ConfigClient> it = ConfigMngr.clinetList.iterator();
        while(it.hasNext()){
            ConfigClient config = it.next();
            if (config.type == ConfigClient.ClientType.NONE)
            {
                continue;
            }
            else if (config.type == ConfigClient.ClientType.NONE)
            {
                createOneTcpClint(config);
            }
            else if (config.type == ConfigClient.ClientType.UDP)
            {
                //暂时不做支持
                continue;
            }
        }
    }

    public void createOneTcpClint(ConfigClient configF)
    {
        TcpClientConfig config  = new TcpClientConfig();

        config.ipAddr = configF.ipAddr;
        config.port = configF.port;

        ConnectTcpClient connect = new ConnectTcpClient(config);
        Request request = new Request(connect);
        mClientRequestList.add(request);
    }

    private boolean checkClientRequest()
    {
        Iterator<Request> it = mClientRequestList.iterator();
        while(it.hasNext()){
            Request request = it.next();
            //客户端就一直尝试连接
            if (request.isColsed())
            {
                request.reconnect();
            }
        }

        return  true;
    }

    private boolean checkServerRequest()
    {
        //由服务端连接进来的 则断开后就断开了, 直接关闭了
        Iterator<Request> it = mServerRequestList.iterator();
        while(it.hasNext()){
            Request request = it.next();
            if (request.isColsed())
            {
                request.Quit();
                it.remove();
            }
        }

        return  true;
    }

    @Override
    public boolean threadProcess()
    {
        checkClientRequest();
        checkServerRequest();
        return  false;
    }
}
