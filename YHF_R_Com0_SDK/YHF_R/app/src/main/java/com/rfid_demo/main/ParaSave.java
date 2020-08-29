package com.rfid_demo.main;

import android.content.Context;
import android.content.SharedPreferences;

class ParaSave {
    public static String adminPsw = "admin";
    private Context context;

    public ParaSave(Context context) {
        this.context = context;
    }


    public void saveTagFileName(String tagFileName) {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("tagFileName", tagFileName);
        editor.commit();

    }

    public String getTagFileName() {
        String tagFileName = "";
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        tagFileName = shared.getString("tagFileName", "");
        return tagFileName;
    }

    public void saveTagPassword(String tagPassword) {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("tagPassword", tagPassword);
        editor.commit();

    }

    public String getTagPassword() {
        String tagPassword = "";
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        tagPassword = shared.getString("tagPassword", "");
        return tagPassword;
    }

    public void saveSingleEpc(String epcStr) {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("singleEpc", epcStr);
        editor.commit();

    }

    public String getSaveSingleEpc() {
        String tagPassword = "";
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        tagPassword = shared.getString("singleEpc", "E200001615120230129096BA");
        return tagPassword;
    }

    public void saveSingleTid(String tidStr) {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("singleTid", tidStr);
        editor.commit();

    }

    public String getSaveSingleTid() {
        String tagPassword = "";
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        tagPassword = shared.getString("singleTid", "E200341201391700033896BA");
        return tagPassword;
    }

    public void saveMulti(boolean Multi) {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean("Multi", Multi);
        editor.commit();

    }

    public boolean getMulti() {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        boolean ret = shared.getBoolean("Multi", false);
        return ret;
    }

    public boolean getTid() {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        boolean ret = shared.getBoolean("ByTid", false);
        return ret;
    }

    public void saveTid(boolean tid) {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean("ByTid", tid);
        editor.commit();

    }

    public void saveFilter(boolean Multi) {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean("Filter", Multi);
        editor.commit();

    }

    public boolean getFilter() {
        SharedPreferences shared = context.getSharedPreferences("para", Context.MODE_PRIVATE);
        boolean ret = shared.getBoolean("Filter", true);
        return ret;
    }
}
