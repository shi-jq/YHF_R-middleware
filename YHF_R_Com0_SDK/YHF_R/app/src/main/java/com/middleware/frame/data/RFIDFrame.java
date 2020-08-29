package com.middleware.frame.data;


public class RFIDFrame {
    private RFrame mSendFrame = null;
    private RFrameList mRevFrameList = null;

    public RFIDFrame() {
        this.mSendFrame = new RFrame();
        this.mRevFrameList = new RFrameList();
        this.mRevFrameList.Initialize();
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