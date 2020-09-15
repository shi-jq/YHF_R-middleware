package com.rfid_demo.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.middleware.config.ConfigMngr;
import com.middleware.request.RequestMngr;
import com.rfid_demo.main.MainActivity;
import com.rfid_demo.main.MyApplication;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Process;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }
}


