package com.middleware.request;

import com.middleware.connect.ConnectConfig;
import com.middleware.frame.common.BaseThread;

import java.util.LinkedList;

public class RequestMngr extends BaseThread {


    private LinkedList<Request> mRequestList = new LinkedList<Request>();

    RequestMngr()
    {
        super("RequestMngr",true);

    }

    void createRequest(ConnectConfig config)
    {

    }


    @Override
    public void threadProcess() {

    }
}
