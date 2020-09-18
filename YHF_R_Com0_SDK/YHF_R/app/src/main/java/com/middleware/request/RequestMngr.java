package com.middleware.request;

import android.util.Log;

import com.middleware.config.ConfigClient;
import com.middleware.config.ConfigMngr;
import com.middleware.frame.common.BaseThread;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;

import java.io.IOException;

public class RequestMngr extends BaseThread {
    //通过串口连接的
    private RequestSerial mSerialRequest = null;
    private RequestTcpServer mTcpServer = null;
    private RequestTcpClient mTcpClient = null;
    private static RequestMngr requestMngr = null;

    @Override
    public void onDestory() {
        mSerialRequest = null;
        mTcpServer = null;
        mTcpClient = null;
        requestMngr = null;
        super.onDestory();
    }

    public static RequestMngr getInstance() {
        if (null == requestMngr) {
            synchronized (RequestMngr.class) {
                if (null == requestMngr) {
                    requestMngr = new RequestMngr();
                }
            }
        }
        return requestMngr;
    }

    public RequestMngr() {
        super("RequestMngr", false);
    }

    public void sendToPC(RFIDFrame rfidFrame, RequestModel.ReqType type) {
        try {
            if (mTcpServer != null && type ==  RequestModel.ReqType.SERVER) {
                mTcpServer.sendToPC(rfidFrame);
            }

            if (mTcpClient != null && type ==  RequestModel.ReqType.CLIENT) {
                mTcpClient.sendToPC(rfidFrame);
            }

            if (mSerialRequest != null && type ==  RequestModel.ReqType.SERIAL) {
                mSerialRequest.sendToPC(rfidFrame);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToPC(RFrame rFrame,  RequestModel.ReqType  to) {
        RFIDFrame rfidFrame = new RFIDFrame(rFrame);
        this.sendToPC(rfidFrame, to);
    }

    @Override
    public boolean threadProcess() throws InterruptedException {
        if (mTcpServer == null) {
            try {
                mTcpServer = new RequestTcpServer(ConfigMngr.getInstance().server.port);
            } catch (IOException e) {
                Log.i("Req Manager","TCP Sever invaild");
                e.printStackTrace();
            }
        }

//        if (mTcpClient == null) {
//            try {
//                mTcpClient = new RequestTcpClient(ConfigMngr.getInstance().client.ipAddr, ConfigMngr.getInstance().client.port);
//            } catch (Exception e) {
//                Log.i("Req Manager","TCP Sever invaild");
//                e.printStackTrace();
//            }
//
//        }

        if (mSerialRequest == null) {
            try {
                mSerialRequest = new RequestSerial();
            } catch (Exception e) {
                Log.i("Req Manager","RequestSerial invaild");
                e.printStackTrace();
            }
        }

        return true;
    }
}
