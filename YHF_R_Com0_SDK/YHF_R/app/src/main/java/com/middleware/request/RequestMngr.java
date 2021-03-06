package com.middleware.request;

import android.util.Log;

import com.middleware.config.ConfigMngr;
import com.middleware.config.ConfigUpload;
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
            Log.e("sendToPC", e.toString());
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
                mTcpServer = new RequestTcpServer(ConfigMngr.getInstance().server.tcp_port);
            } catch (IOException e) {
                mTcpServer = null;
                Log.i("Req Manager","TCP Sever invaild");
                e.printStackTrace();
            }
        }

        int dataPush = ConfigMngr.getInstance().upload.dataPush;

        if (!ConfigMngr.getInstance().client.ipAddr.startsWith("127")
                && (dataPush == ConfigUpload.PORT_TYPE.PORT_TYPE_TCP.GetValue())) {

            if (mTcpClient == null)
            {
                mTcpClient = new RequestTcpClient(ConfigMngr.getInstance().client.ipAddr, ConfigMngr.getInstance().client.port);
                Log.i("Req Manager","TCP Client success");
            }

            if (!mTcpClient.isConnect())
            {
                mTcpClient.connect();
            }
        }

        if (mSerialRequest == null) {
            try {
                mSerialRequest = new RequestSerial();
            } catch (Exception e) {
                mSerialRequest = null;
                Log.i("Req Manager","RequestSerial invaild");
                e.printStackTrace();
            }
        }

        return false;
    }
}
