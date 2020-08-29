package com.middleware.frame.main;

import java.util.Observable;

public class FrameMsgObervable  extends Observable {


    public void postNewVersion(String version) {
        setChanged(); // 标示内容发生改变
        notifyObservers(version);// 通知所有观察者
    }

}
