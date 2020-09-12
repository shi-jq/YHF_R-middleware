package com.middleware.config;

import com.rfid_demo.ctrl.Util;

//用来存储 与 pc端连接的串口配置
public class ConfigPcSerial {

    private static final String COM_NUM_KEY = "ConfigPcSerial_COM_NUM_KEY";
    private static final String BAUD_RATE_KEY = "ConfigPcSerial_BAUD_RATE_KEY";

    public int comNum = 1;
    public int baudrate = 115200;


    public ConfigPcSerial()
    {
        this.comNum = (int) Util.dtGet(COM_NUM_KEY,1);
        this.baudrate = (int) Util.dtGet(BAUD_RATE_KEY,115200);
    }
}
