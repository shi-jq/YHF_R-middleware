package com.middleware.frame.common;

public class RFIDSystemCfg {
    public PORT_TYPE nPortType;
    public byte nBusAddr;
    public int nBaud;
    public int nComNum;

    public RFIDSystemCfg() {
        this.nPortType = PORT_TYPE.COM_PORT;
        this.nBusAddr = 0;
        this.nBaud = 115200;
        this.nComNum = 0;
    }

    public enum PORT_TYPE {
        COM_PORT
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\RFIDSystemCfg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */