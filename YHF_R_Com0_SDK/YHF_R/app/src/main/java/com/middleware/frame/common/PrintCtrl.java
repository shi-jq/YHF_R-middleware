package com.middleware.frame.common;

import android.util.Log;


public class PrintCtrl {
    private static boolean EnablePrint = true;
    private static boolean EnablePrintBUff = true;

    public static void PrintBuffer(byte[] buffer, int offeset, int count) {
        PrintBuffer("", buffer, 0, count);
    }


    public static void PrintBUffer(String title, byte[] buffer, int count) {
        PrintBuffer(title, buffer, 0, count);
    }


    public static void PrintBUffer(String title, byte[] buffer) {
        PrintBuffer(title, buffer, 0, buffer.length);
    }


    public static void PrintBuffer(String title, byte[] buffer, int offeset, int count) {
        if (EnablePrintBUff) {
            String tmp = "";
            tmp = "len: ";
            tmp = tmp + count;
            tmp = tmp + ";  ";
            for (int i = offeset; i < count; i++) {
                tmp = tmp + " ";
                tmp = tmp + Integer.toHexString(buffer[i] & 0xFF);
            }
            tmp = tmp + "\n";

            PrintStr(title, tmp);
        }
    }

    public static void PrintStr(String title, String Msg) {
        if (EnablePrint) {
            Log.v(title, Msg);
        }
    }

    public static void PrintStr(String Msg) {
        if (EnablePrint)
            Log.v(" ", Msg);
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\PrintCtrl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */