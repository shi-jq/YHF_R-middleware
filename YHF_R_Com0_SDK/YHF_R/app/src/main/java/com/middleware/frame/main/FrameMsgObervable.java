package com.middleware.frame.main;

import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;

import java.util.Observable;

public class FrameMsgObervable  extends Observable {


    public void sendFrame(RFIDFrame frame) {
        setChanged(); // 标示内容发生改变
        notifyObservers(frame);// 通知所有观察者
    }

}
