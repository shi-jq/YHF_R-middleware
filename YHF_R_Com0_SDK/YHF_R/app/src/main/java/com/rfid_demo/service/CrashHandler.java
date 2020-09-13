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
        Intent intent = new Intent();
        Context context = MyApplication.contxt;
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC,System.currentTimeMillis() + 1000,pendingIntent);

        Process.killProcess(Process.myPid());
        System.exit(0);

    }
}


