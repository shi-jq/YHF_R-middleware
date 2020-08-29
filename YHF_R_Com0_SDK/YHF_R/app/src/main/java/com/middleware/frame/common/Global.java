package com.middleware.frame.common;

public class Global {
    public static final int TX_START_IDLE_MSECS = 2;
    public static final int TX_IDLE_MSECS = 100;
    public static final int TX_PKT_MAX = 64;
    public static final int MAC_RXBUF_SIZE = 256;
    public static final int ENUMERATION_TIMEOUT = 1000;
    public static final int COMMAND_TIMEOUT_MS = 5000;
    public static final TransportVersion gTransLibVersion = new TransportVersion(
            TLibVer.TRANSLIB_MAJOR_VSN.GetValue(),
            TLibVer.TRANSLIB_MINOR_VSN.GetValue(),
            TLibVer.TRANSLIB_MAINTENANCE_VSN.GetValue(),
            TLibVer.TRANSLIB_RELEASE_VSN.GetValue());
    public static final int RFID_READ_TIMEOUT = 3000;
    public static final int TXCHAR_SER_MAX_TRANSFER = 4096;
    public static int g_droppedDev = 0;
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\Global.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */