package com.middleware.config;

import com.middleware.frame.common.INT8U;
import com.middleware.frame.data.RFrame;
import com.rfid_demo.ctrl.Util;

import java.util.HashMap;
import java.util.Map;

//用来存储 与 pc端连接的串口配置
public class ConfigPcSerial {

    public static final String COM_NUM_KEY = "ConfigPcSerial_COM_NUM_KEY";
    public static final String BAUD_RATE_KEY = "ConfigPcSerial_BAUD_RATE_KEY";
    public static final int BAUD_RATE_DEFAULT = 115200;
    public static final int BUS_ADDR_DEFAULT = 0;
    public static final Map<Integer, Integer> rateMap = new HashMap();
    public static final String BUS_RATE_KEY = "ConfigPcSerial_BUS_RATE_KEY";
    public int baudrate = BAUD_RATE_DEFAULT;
    public int busadddr = 0;

    public ConfigPcSerial() {
        this.baudrate = (int) Util.dtGet(BAUD_RATE_KEY, BAUD_RATE_DEFAULT);
        this.busadddr = (int) Util.dtGet(BUS_RATE_KEY, BUS_ADDR_DEFAULT);
        rateMap.put(0, 4800);
        rateMap.put(1, 9600);
        rateMap.put(2, 19200);
        rateMap.put(3, 38400);
        rateMap.put(4, 57600);
        rateMap.put(5, 115200);
    }

    public static byte[] baudRateBytes() {
        byte[] ret = new byte[1];
        int baudrateIndex = (int) Util.dtGet(BAUD_RATE_KEY, BAUD_RATE_DEFAULT);
        int baudrate = 5;
        for (int key : rateMap.keySet()) {
            if (rateMap.get(key) == baudrateIndex) {
                baudrate = key;
                break;
            }
        }

        INT8U n8u = new INT8U(baudrate);
        ret[0] = n8u.GetValue();
        return ret;
    }

    public static byte[] busaddrBytes() {
        byte[] ret = new byte[1];
        int busAddr = (int) Util.dtGet(BUS_RATE_KEY, BUS_ADDR_DEFAULT);
        INT8U n8u = new INT8U(busAddr);
        ret[0] = n8u.GetValue();
        return ret;
    }

    public static boolean configBaudRateWithRFrame(RFrame pRFrame) {
        byte[] data = pRFrame.GetData();

        byte rateIndex = data[1];
        if (rateMap.containsKey((int) rateIndex)) {
            int baudrate = rateMap.get((int) rateIndex);
            Util.dtSave(BAUD_RATE_KEY, baudrate);
        } else {
            return false;
        }

        return true;
    }

    public static boolean configBusAddrWithRFrame(RFrame pRFrame) {
        byte[] data = pRFrame.GetData();

        int busAddr = data[1];
        Util.dtSave(BUS_RATE_KEY, busAddr);
        return true;


    }

}
