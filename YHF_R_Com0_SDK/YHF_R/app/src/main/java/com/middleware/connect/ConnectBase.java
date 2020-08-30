package com.middleware.connect;

import java.io.InputStream;
import java.io.OutputStream;

public interface ConnectBase
{
    String getConnectName();
    boolean init();
    boolean open();
    boolean close();
    boolean quit();
    boolean isColsed();
    boolean reconnect();

    InputStream getInputStream();
    OutputStream getOutputStream();
}
