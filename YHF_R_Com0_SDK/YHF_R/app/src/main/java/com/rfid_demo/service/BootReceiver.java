package com.rfid_demo.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.rfid_demo.ctrl.Util;

import com.rfid_demo.main.MainActivity;

public class BootReceiver extends BroadcastReceiver
{
    private  static boolean isBoot = false;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (!isBoot){
            Util.restartApp();
        }
    }
}
