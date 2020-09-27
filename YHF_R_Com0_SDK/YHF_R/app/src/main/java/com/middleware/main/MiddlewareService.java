package com.middleware.main;

import com.middleware.config.ConfigMngr;
import com.middleware.executor.Executor;
import com.middleware.frame.common.BaseThread;
import com.middleware.reader.Reader;
import com.middleware.request.RequestMngr;

public class MiddlewareService extends BaseThread {

    private ConfigMngr config =  ConfigMngr.getInstance();
    private RequestMngr requesstMngr = RequestMngr.getInstance();
    private Reader reader = Reader.getInstance();
    private Executor executor = Executor.getInstance();


    public  MiddlewareService() {
        super("MiddlewareService",false);
    }

    @Override
    public boolean threadProcess() throws InterruptedException {
        return false;
    }
}
