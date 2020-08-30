package com.middleware.request;

import com.middleware.connect.ConnectSerial;
import com.middleware.connect.SerialConfig;
import com.middleware.frame.common.BaseThread;

import java.util.Iterator;
import java.util.LinkedList;

public class RequestMngr extends BaseThread {


    private LinkedList<Request> mRequestList = new LinkedList<Request>();

    RequestMngr()
    {
        super("RequestMngr",true);

    }

    private  void createSerialPCRequest()
    {
        SerialConfig config  = new SerialConfig();
        ConnectSerial connect = new ConnectSerial(config);
        Request request = new Request(connect,true);
        mRequestList.add(request);
    }

    private void createTcpClientRequest()
    {

    }


    @Override
    public boolean threadProcess()
    {
        Iterator<Request> it = mRequestList.iterator();
        while(it.hasNext()){
            Request request = it.next();
            if (request.isNeedQuit())
            {
                request.Quit();
                it.remove();
            }
        }
        return  false;
    }
}
