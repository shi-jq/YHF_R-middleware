package com.rfid_demo.ctrl;

import com.rfid_demo.main.MainActivity;

public class AppCfg {

    public static MainActivity mMain = null;

    public static String getString(int id) {
        if (mMain != null) {
            return mMain.getString(id);
        }
        return null;
    }

    public static void ShowMsg(String msg, boolean isFaild) {
        if (mMain != null) {
            mMain.ShowMsg(msg, isFaild);
        }
    }

    public static void ShowMsg(int strId, boolean isFaild) {
        if (mMain != null) {
            mMain.ShowMsg(getString(strId), isFaild);
        }
    }

}
