package com.middleware.frame.main;

public class MsgMngr
{

    public static FrameMsgObervable PcToAndroidObv;    //PC->android平台
    public static FrameMsgObervable AndroidToPcObv; //android平台->PC
    public static FrameMsgObervable AndroidToModelObv;//android平台->读写模块
    public static FrameMsgObervable ModelToAndroidObv;//模块->android平台
    public static ReaderStatusObervable ReaderStatusObv;//读写器状态

}
