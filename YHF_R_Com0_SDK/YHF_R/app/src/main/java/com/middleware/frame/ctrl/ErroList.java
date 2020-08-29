package com.middleware.frame.ctrl;


import com.middleware.frame.ctrl.RfidErro;
import com.middleware.frame.common.*;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ErroList {
    private Lock mLock = new ReentrantLock();
    private LinkedList<RfidErro> mErroList = new LinkedList<RfidErro>();


    public void addErro(RfidStatus error) {
        RfidErro pRfidErro = new RfidErro();
        pRfidErro.erroCode = error;

        this.mLock.lock();
        this.mErroList.addLast(pRfidErro);
        this.mLock.unlock();
    }


    public void ClearAll() {
        this.mLock.lock();
        this.mErroList.clear();
        this.mLock.unlock();
    }


    public RfidErro GetLastErro() {
        RfidErro pRfidErro = null;
        this.mLock.lock();
        if (this.mErroList.size() > 0) {
            pRfidErro = this.mErroList.removeLast();
        }
        this.mLock.unlock();
        return pRfidErro;
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\reader\ctrl\ErroList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */