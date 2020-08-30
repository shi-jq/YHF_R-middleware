package com.middleware.frame.data;

import com.middleware.frame.ctrl.RfidCommand;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RFrameList {
    private LinkedList<RFrame> mRFrameList =  new LinkedList<RFrame>();
    private Lock mLock  = new ReentrantLock();

    public int GetCount() {
        int count = 0;
        this.mLock.lock();
        count = this.mRFrameList.size();
        this.mLock.unlock();
        return count;
    }


    public void AddRFrame(RFrame pRFrame) {
        this.mLock.lock();
        this.mRFrameList.addLast(pRFrame);
        this.mLock.unlock();
    }


    public RFrame RemoveRFrame() {
        RFrame pRFrame = null;
        this.mLock.lock();

        if (!this.mRFrameList.isEmpty()) {
            pRFrame = this.mRFrameList.removeFirst();
        }

        this.mLock.unlock();
        return pRFrame;
    }


    public RFrame GetFirstRFrame() {
        RFrame pRFrame = null;
        this.mLock.lock();

        if (!this.mRFrameList.isEmpty()) {
            pRFrame = this.mRFrameList.getFirst();
        }

        this.mLock.unlock();
        return pRFrame;
    }


    public RFrame GetLastRFrame() {
        RFrame pRFrame = null;
        this.mLock.lock();

        if (!this.mRFrameList.isEmpty()) {
            pRFrame = this.mRFrameList.getLast();
        }

        this.mLock.unlock();
        return pRFrame;
    }


    public RFrame GetRFrame(int index) {
        RFrame pRFrame = null;
        this.mLock.lock();

        if (!this.mRFrameList.isEmpty()) {
            pRFrame = this.mRFrameList.get(index);
        }

        this.mLock.unlock();
        return pRFrame;
    }

    public RFrame GetRFrame(byte byteCommand,byte busaddr, boolean remove) {
        RFrame pRFrame = null;
        this.mLock.lock();

        Iterator<RFrame> iter = this.mRFrameList.iterator();
        while (iter.hasNext()) {
            RFrame rFrame = iter.next();
            if (rFrame.GetRfidCommand() == byteCommand
                    && rFrame.GetBusAddr() == busaddr) {
                pRFrame = rFrame;
                if (remove)
                {
                    mRFrameList.remove(rFrame);
                }
                break;
            }
        }
        this.mLock.unlock();
        return pRFrame;
    }


    public RFrame GetRFrame(RfidCommand pCommand, boolean remove) {
        RFrame pRFrame = null;
        this.mLock.lock();

        Iterator<RFrame> iter = this.mRFrameList.iterator();
        while (iter.hasNext()) {
            RFrame rFrame = iter.next();
            if (rFrame.GetRfidCommand() == pCommand.GetValue()) {
                pRFrame = rFrame;
                if (remove)
                {
                    mRFrameList.remove(rFrame);
                }
                break;
            }
        }
        this.mLock.unlock();
        return pRFrame;
    }

    public boolean DelRFrame(RfidCommand pCommand) {
        boolean retB = false;
        RFrame pFrame = GetRFrame(pCommand,true);
        if (pFrame != null) {

            retB = true;
        }

        return retB;
    }


    public void ClearAll() {
        this.mLock.lock();
        this.mRFrameList.clear();
        this.mLock.unlock();
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\reader\data\RFrameList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */