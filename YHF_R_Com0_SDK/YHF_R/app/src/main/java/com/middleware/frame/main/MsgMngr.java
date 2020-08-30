package com.middleware.frame.main;

public class MsgMngr
{
    //命令
    public static FrameMsgObervable PcToAndroidMsgObv;    //PC->android平台
    public static FrameMsgObervable AndroidToModelMsgObv;//android平台->读写模块

    public static FrameTagObervable AndroidToPcTagObv; //android平台->PC
    public static FrameTagObervable ModelToAndroidTagObv;//模块->android平台

    public static ReaderStatusObervable ReaderStatusObv;//读写器状态

}
