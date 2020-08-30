package com.middleware.frame.data;


public class RFIDFrame {
    private RFrame mSendFrame = null;
    private RFrameList mRevFrameList = new RFrameList();

    public RFIDFrame() {
        this.mSendFrame = new RFrame();
    }

    public RFIDFrame(RFrame sendFrame) {
        this.mSendFrame = sendFrame;
    }

    public RFIDFrame(RFrame sendFrame,RFrame recvFrame) {
        this.mSendFrame = sendFrame;
        mRevFrameList.AddRFrame(recvFrame);
    }

    public RFrame GetSendFrame() {
        return this.mSendFrame;
    }


    public void AddRevFrame(RFrame pRFrame) {
        this.mRevFrameList.AddRFrame(pRFrame);
    }


    public RFrame RemoveRevFrame() {
        return this.mRevFrameList.RemoveRFrame();
    }


    public RFrame GetRevFrame() {
        return this.mRevFrameList.GetFirstRFrame();
    }


    public void ClearRevFrame() {
        this.mRevFrameList.ClearAll();
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\reader\data\RFIDFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */