package com.middleware.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.pda.serialport.SerialPort;

public class ConnectSerial implements ConnectBase{

    public class SerialConfig
    {
        public int comNum = 0;
        public int baudrate = 115200;
    }

    private SerialPort mSerialPort = null;
    private SerialConfig mSysConfig = null;

    void ConnectSerial(SerialConfig config)
    {
        mSysConfig = config;
    }


    @Override
    public boolean init() {

        try {
            this.mSerialPort = new SerialPort(mSysConfig.comNum, mSysConfig.baudrate, 0);
        } catch (IOException e) {
            this.mSerialPort = null;
            return false;
        }

        return true;
    }

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public boolean close() {
        try {
            mSerialPort.getInputStream().close();
            mSerialPort.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSerialPort.close(mSysConfig.comNum);
        return true;
    }

    @Override
    public boolean quit() {
        return true;
    }

    @Override
    public boolean isColsed() {
        return false;
    }

    @Override
    public boolean reconnect() {
        return true;
    }

    @Override
    public InputStream getInputStream() {
        assert (mSerialPort != null);
        return mSerialPort.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        assert (mSerialPort != null);
        return mSerialPort.getOutputStream();
    }
}
