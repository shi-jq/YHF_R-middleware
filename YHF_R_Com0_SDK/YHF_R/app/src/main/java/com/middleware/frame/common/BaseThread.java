package com.middleware.frame.common;

import android.util.Log;

public abstract class BaseThread implements Runnable {
    public static final int SUSPEND_TIME_MILLISECONDS = 5000;

    public boolean isNeedStop = false;
    private String name;
    private Thread mThread;

    private boolean suspendFlag = false;// 控制线程的执行
    // private int i = 0;
    private String TAG = getName();

    /**
     * 构造函数
     *
     * @param name    线程名称。
     * @param suspend 初始化是否暂停。
     */
    public BaseThread(String name, boolean suspend) {
        suspendFlag = suspend;
        this.name = name;
        mThread = new Thread(this, name);
        System.out.println("new Thread: " + mThread);
        mThread.start();
    }

    public void run() {
        try {
            synchronized (this) {
                while (!isNeedStop) {
                    // System.out.println(name + ": " + i++);
                    while (suspendFlag) {
                        wait();
                    }
                    if (!isNeedStop && !threadProcess()) {
                        Thread.sleep(SUSPEND_TIME_MILLISECONDS);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            onDestory();
        }
        Log.i(TAG, name + " exited");
    }

    /**
     * 线程处理接口。返回true则不等待, false则会等待
     */
    public abstract boolean threadProcess() throws InterruptedException;

    /**
     * 线程暂停
     */
    public void suspend() {
        this.suspendFlag = true;
    }

    /**
     * 唤醒线程
     */
    public synchronized void resume() {
        this.suspendFlag = false;
        notify();
    }

    /**
     * 返回线程名
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 获取线程对象。
     *
     * @return 线程对象。
     */
    public Thread getT() {
        return mThread;
    }

    /**
     * 停止线程运行。
     */
    public void stop() {
        if (mThread != null) {

            mThread.interrupt();
            mThread = null;
        }
    }

    /**
     * 线程处理接口。
     */
    public void onDestory() {
        Log.i(TAG, name + " destory!");
    }

}