package com.rfid_demo.service;

import android.content.Intent;

import com.rfid_demo.main.MyApplication;

public class RFIDCMD
{
    // 用于设置本次关机时间，表示 2018/1/1 8:30 关机 int[] timeoff={2014,1,1,8,30};
    //用于设置下次开机时间， 表示 2018/1/1 20:00 开机 int[] timeon ={2014,1,1,20,30};
        public static void ctrllOnAndOff(int[] timeOn, int[] timeoff)
        {
            Intent intent = new Intent("com.mrk.setpoweronoff");
            intent.putExtra("timeon", timeOn);
            intent.putExtra("timeoff", timeoff);
            intent.putExtra("enable", true); //使能开关机功能， 设为 false,则为关闭，
            MyApplication.contxt.sendBroadcast(intent);
        }

    public static void reboot()
    {
        Intent intent = new Intent("reboot.zysd.now");
        MyApplication.contxt.sendBroadcast(intent);
    }

    public static void shutdown() {
        Intent intent = new Intent("shutdown.zysd.now");
        MyApplication.contxt.sendBroadcast(intent);
    }

}
