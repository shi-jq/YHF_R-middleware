package com.rfid_demo.ctrl;


import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.INT8U;

public class ApiCtrl {
    public static boolean mIsOpen = false;
    public static boolean mIsReading = false;

    public static boolean GetIsOpen() {
        return mIsOpen;
    }

    public static boolean Initialize() {
       return false;
    }

    //
    public static boolean Quit() {
        return false;
    }

      public static boolean Open() {
        return false;
    }

    public static boolean Close() {
        return false;
    }

    public static boolean SAATYAntennaParmSet(byte[] pPara, int nLen) {
        return false;
    }

    public static boolean SAATYRFParaQuery(int nType, INT8U nParam) {
        return false;
    }

    public static boolean SAATYRFParaSet(int nType, int nLen) {
        return false;
    }

    public static boolean SAATYAntennaParmQuery(INT8U pPowerRecvPlus,
                                                INT8U p2401FreqSendPwr, INT8U pAttenuatorPower) {
        return false;
    }

    public static boolean SAATYMakeTagUpLoadIDCode(int nOpType, int nIDType) {
        return false;
    }

    public static int SAATYRevIDMsgDecExpand(INT8U nTagType, INT32U pId, INT32U nRSSI, INT32U nParam1) {
        return 0;
    }

    public static boolean SAATPowerOff() {
        return false;
    }

    public static String SAATCopyright() {
        return "";
    }

    public static boolean SAAT_YTagSelect(byte nOpEnable, byte nMatchType,
                                          byte[] MatchData, byte nLenth) {
        return false;
    }
}
