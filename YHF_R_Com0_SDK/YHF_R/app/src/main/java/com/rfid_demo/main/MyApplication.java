package com.rfid_demo.main;

import android.app.Application;
import android.content.Context;
import com.rfid_demo.ctrl.Util;
import com.rfid_demo.service.CrashHandler;
import java.util.Vector;

public class MyApplication extends Application {
    public static final String READER_UHF = "READER_UHF";
    public static final String READER_NFC = "READER_NFC";
    public String ReaderType = READER_NFC;
    public Vector<String> listSearchEpc = new Vector<String>();
    public Vector<String> listSearchTid = new Vector<String>();
    public String pswordStr = "";
    private ParaSave para;

    public static Context contxt;

    @Override
    public void onCreate() {
        super.onCreate();
        para = new ParaSave(this);
        Util.dtContext =   getApplicationContext();
        contxt = getApplicationContext();
//        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }

    public ParaSave getParaSave() {
        return para;
    }

    public void rereadPsw() {
        pswordStr = para.getTagPassword();
    }

}
