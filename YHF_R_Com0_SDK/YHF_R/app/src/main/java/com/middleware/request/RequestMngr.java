package com.middleware.request;

import com.middleware.config.ConfigClient;
import com.middleware.config.ConfigMngr;
import com.middleware.frame.common.BaseThread;

import java.util.Iterator;

public class RequestMngr extends BaseThread {
    //通过串口连接的
    private RequestSerial mSerialRequest = null;


    public RequestMngr()
    {
        super("RequestMngr",true);

        recreateSerialRequest();
    }

    //重新创建串口, 串口波特率改变的时候 需要去调整
    private  void recreateSerialRequest()
    {

    }

    //重新设定转发的客户端
    private void recreatAllClient()
    {
        clearAllClient();
        createAllClient();
    }

    private void clearAllClient()
    {
//        Iterator<Request> it = mClientRequestList.iterator();
//        while(it.hasNext()){
//            Request request = it.next();
//            request.Quit();
//        }
//        mClientRequestList.clear();
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

    }

    private boolean checkClientRequest()
    {


        return  true;
    }

    private boolean checkServerRequest()
    {

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
