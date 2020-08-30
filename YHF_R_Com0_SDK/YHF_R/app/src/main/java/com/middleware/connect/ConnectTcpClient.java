package com.middleware.connect;

import java.io.InputStream;
import java.io.OutputStream;

public class ConnectTcpClient implements ConnectBase{

    private  TcpClientConfig mSysConfig;
    public ConnectTcpClient(TcpClientConfig config)
    {
        this.mSysConfig = config;
    }

    @Override
    public String getConnectName() {
        return mSysConfig.ipAddr+":" + mSysConfig.port;
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public boolean open() {
        return false;
    }

    @Override
    public boolean close() {
        return false;
    }

    @Override
    public boolean quit() {
        return false;
    }

    @Override
    public boolean isColsed() {
        return false;
    }

    @Override
    public boolean reconnect() {
        return false;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }
}
