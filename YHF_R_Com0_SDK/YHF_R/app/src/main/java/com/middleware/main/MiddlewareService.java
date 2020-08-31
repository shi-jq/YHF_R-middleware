package com.middleware.main;

import com.middleware.config.ConfigMngr;
import com.middleware.executor.Executor;
import com.middleware.frame.common.BaseThread;
import com.middleware.reader.Reader;
import com.middleware.request.RequestMngr;

import java.io.IOException;

public class MiddlewareService extends BaseThread {

    private ConfigMngr config = new ConfigMngr();
    private RequestMngr requesstMngr = new RequestMngr();
    private Reader reader = new Reader();
    private Executor executor = new Executor();


    MiddlewareService() throws IOException {
        super("MiddlewareService",false);


    }


    @Override
    public boolean threadProcess()
    {
        return  false;
    }
}
