package com.middleware.main;

import com.middleware.frame.common.BaseThread;
import com.middleware.frame.common.PrintCtrl;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.request.Request;

import java.io.IOException;
import java.io.InputStream;

public class MiddlewareService extends BaseThread {

    MiddlewareService()
    {
        super("MiddlewareService",false);
    }

    @Override
    public boolean threadProcess() {
        return  false;
    }
}
