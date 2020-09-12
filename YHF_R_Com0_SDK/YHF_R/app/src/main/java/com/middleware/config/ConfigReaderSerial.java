package com.middleware.config;

import com.rfid_demo.ctrl.Util;

//用来存储与读写器 相关的配置
public class ConfigReaderSerial {

    public static final String COM_NUM_KEY = "ConfigReaderSerial_COM_NUM_KEY";
    public static final String BAUD_RATE_KEY = "ConfigReaderSerial_BAUD_RATE_KEY";

    public int comNum = 1;
    public int baudrate = 115200;

    public ConfigReaderSerial()
    {
        this.comNum = (int) Util.dtGet(COM_NUM_KEY,1);
        this.baudrate = (int) Util.dtGet(BAUD_RATE_KEY,115200);
    }
}
