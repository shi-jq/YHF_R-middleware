package com.middleware.request;

import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;

public class RequestModel{
    public enum ReqType {
        SERVER,
        CLIENT,
        SERIAL;
    }

    public static final int SuccessHandler = 0;
    public static final int FailHandler = -1;

    public  RFrame pFrame;
    public DataProc mProc = new DataProc();
    public ReqType type;
    public RequestModel(RFrame pFrame,DataProc mProc, ReqType type)
    {
        this.pFrame = pFrame;
        this.mProc = mProc;
        this.type = type;
    }

    public RFIDFrame settingResFrame()
    {
       return settingResFrame((byte)0x00);
    }

    public RFIDFrame settingResFrame(byte resVal)
    {
        RFIDFrame rfidFrame = new RFIDFrame(this.pFrame);
        RFrame pRecvRFrame = this.mProc.createRecvRFrame(this.pFrame,  resVal);
        rfidFrame.AddRevFrame(pRecvRFrame);
        return rfidFrame;
    }

    public RFIDFrame queryResFrame(byte[] data)
    {
        RFIDFrame rfidFrame = new RFIDFrame(this.pFrame);
        RFrame pRecvRFrame = this.mProc.createRecvRFrame(this.pFrame, data, (byte)0x00);
        rfidFrame.AddRevFrame(pRecvRFrame);
        return rfidFrame;
    }

    public RFIDFrame queryResFrame(byte[] data, byte resVal)
    {
        RFIDFrame rfidFrame = new RFIDFrame(this.pFrame);
        RFrame pRecvRFrame = this.mProc.createRecvRFrame(this.pFrame, data,  resVal);
        rfidFrame.AddRevFrame(pRecvRFrame);
        return rfidFrame;
    }

}



