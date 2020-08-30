package com.middleware.frame.main;

import com.middleware.frame.ctrl.ReaderState;
import com.middleware.frame.data.RFIDFrame;

import java.util.Observable;

public class ReaderStatusObervable  extends Observable {

    public void sendFrame(ReaderState status) {
        setChanged(); // 标示内容发生改变
        notifyObservers(status);// 通知所有观察者
    }
}
