package com.middleware.frame.main;

public class MsgMngr
{
    //命令
    public static FrameMsgObervable PcToAndroidMsgObv = new FrameMsgObervable();    //PC->android平台
    public static FrameMsgObervable AndroidToModelMsgObv = new FrameMsgObervable();//android平台->读写模块

    public static FrameTagObervable AndroidToPcTagObv = new FrameTagObervable(); //android平台->PC
    public static FrameTagObervable ModelToAndroidTagObv = new FrameTagObervable();//模块->android平台

    public static ReaderStatusObervable ReaderStatusObv =  new ReaderStatusObervable();//读写器状态

}
