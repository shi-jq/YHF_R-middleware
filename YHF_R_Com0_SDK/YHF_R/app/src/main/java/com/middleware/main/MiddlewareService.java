package com.middleware.main;

import android.util.Log;

import com.middleware.config.ConfigMngr;
import com.middleware.executor.Executor;
import com.middleware.frame.common.BaseThread;
import com.middleware.reader.Reader;
import com.middleware.request.RequestMngr;

import java.io.IOException;

public class MiddlewareService extends BaseThread {

    private ConfigMngr config = new ConfigMngr();
    private RequestMngr requesstMngr = new RequestMngr();
    private Reader reader = null;
    private Executor executor = new Executor();


    public  MiddlewareService() {
        super("MiddlewareService",false);
    }

    @Override
    public boolean threadProcess()
    {
        //*
        if (reader == null)
        {
            try {
                reader = new Reader();
            } catch (IOException e) {
                e.printStackTrace();
                reader = null;
            }
        }

        Log.i("haha","threadProcess");
         //*/
        return  false;
    }
}
