package com.middleware.func;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;

public class APNUtli {

    public static Uri APN_URI = Uri.parse("content://telephony/carriers");
    public static Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    private static final String[] APN_PROJECTION = {
            Telephony.Carriers.TYPE,            // 0
            Telephony.Carriers.MMSC,            // 1
            Telephony.Carriers.MMSPROXY,        // 2
            Telephony.Carriers.MMSPORT          // 3
    };

    public static int addAPN(Context context, String apn,String account, String pwd) {
        int id = -1;
        String NUMERIC = getSIMInfo(context);
        if (NUMERIC == null) {
            return -1;
        }

        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", apn);                                  //apn中文描述
        values.put("apn", apn);                                     //apn名称
        values.put("type", "default");                            //apn类型
        values.put("numeric", NUMERIC);
        values.put("mcc", NUMERIC.substring(0, 3));
        values.put("mnc", NUMERIC.substring(3, NUMERIC.length()));
        values.put("proxy", "");                                        //代理
        values.put("port", "");                                         //端口
        values.put("mmsproxy", "");                                     //彩信代理
        values.put("mmsport", "");                                      //彩信端口
        values.put("user", account);                                         //用户名
        values.put("server", "");                                       //服务器
        values.put("password",pwd);                                     //密码
        values.put("mmsc", "");                                          //MMSC
        Cursor c = null;
        Uri newRow = resolver.insert(APN_URI, values);
        if (newRow != null) {
            c = resolver.query(newRow, null, null, null, null);
            int idIndex = c.getColumnIndex("_id");
            c.moveToFirst();
            id = c.getShort(idIndex);
        }
        if (c != null)
            c.close();
        return id;
    }

    public static String getSIMInfo(Context context) {
        TelephonyManager iPhoneManager = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return iPhoneManager.getSimOperator();
    }

    // 设置接入点
    public static void setAPN(Context context, int id) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("apn_id", id);
        resolver.update(CURRENT_APN_URI, values, null, null);
    }

    // 删除APN
    public static void deleteAPN(Context context, int id) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(CURRENT_APN_URI, null, null);
    }

    public static int getAPN(Context context, String apn){
        ContentResolver resolver = context.getContentResolver();
        String apn_condition = String.format("apn like '%%%s%%'", apn);
      //  final Cursor apnCursor = SqliteWrapper.query(context, context.getContentResolver(),
      //          Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "current"), APN_PROJECTION, null, null, null);
        Cursor c = resolver.query(APN_URI, null, apn_condition, null, null);

        // 该项APN存在
        if (c != null && c.moveToNext()) {
            int id = c.getShort(c.getColumnIndex("_id"));
            String name1 = c.getString(c.getColumnIndex("name"));
            String apn1 = c.getString(c.getColumnIndex("apn"));

            Log.e("APN has exist", id + name1 + apn1);
            return id;
        }

        return -1;
    }

}
