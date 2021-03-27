package com.middleware.config;

import com.middleware.frame.data.RFrame;
import com.middleware.func.APNUtli;
import com.rfid_demo.ctrl.AppCfg;
import com.rfid_demo.ctrl.Util;
import com.rfid_demo.main.MyApplication;

import java.io.UnsupportedEncodingException;

class ConfigGprs {
    public static final String CONFIGGPRS_APN_KEY = "CONFIGGPRS_APN_KEY";
    public static final String CONFIGGPRS_ACCOUNT_KEY = "CONFIGGPRS_ACCOUNT_KEY";
    public static final String CONFIGGPRS_PWD_KEY = "CONFIGGPRS_PWD_KEY";

    public static final String CONFIGGPRS_APN_DEF = "";
    public static final String CONFIGGPRS_ACCOUNT_DEF = "";
    public static final String CONFIGGPRS_PWD_DEF = "";

    public String  apnSet =  CONFIGGPRS_APN_DEF;
    public String  accountSet =  CONFIGGPRS_ACCOUNT_DEF;
    public String  pwdSet =  CONFIGGPRS_PWD_DEF;


    public ConfigGprs( )
    {
        this.apnSet = (String) Util.dtGet(CONFIGGPRS_APN_KEY, CONFIGGPRS_APN_DEF);
        this.accountSet = (String) Util.dtGet(CONFIGGPRS_ACCOUNT_KEY, CONFIGGPRS_ACCOUNT_DEF);
        this.pwdSet = (String) Util.dtGet(CONFIGGPRS_PWD_KEY, CONFIGGPRS_PWD_DEF);

        if (apnSet != CONFIGGPRS_APN_DEF)
        {
            resetApnToSys();
        }
    }

    public static void resetApnToSys()
    {
        //*/
        String apnSet = (String) Util.dtGet(CONFIGGPRS_APN_KEY, CONFIGGPRS_APN_DEF);
        String accountSet = (String) Util.dtGet(CONFIGGPRS_ACCOUNT_KEY, CONFIGGPRS_ACCOUNT_DEF);
        String pwdSet = (String) Util.dtGet(CONFIGGPRS_PWD_KEY, CONFIGGPRS_PWD_DEF);

        if (apnSet != CONFIGGPRS_APN_DEF)
        {
            String apn_name = apnSet;
            int apn_id = APNUtli.getAPN( MyApplication.contxt, apn_name);
            if (apn_id != -1)
            {
                APNUtli.deleteAPN(MyApplication.contxt,apn_id);
            }

            apn_id = APNUtli.addAPN( MyApplication.contxt, apn_name,accountSet,  pwdSet );
            if( apn_id == -1){
                AppCfg.ShowMsg("设置APN失败",false);
            }

            if( apn_id != -1) {
                APNUtli.setAPN(MyApplication.contxt, apn_id);
            }
        }
        //*/
    }

    public static  void configApnWithRFrame(RFrame pRFrame)
    {
        byte subByte = pRFrame.GetByte(6);
        int count = pRFrame.GetByte(7);

        byte[] valuebyte = pRFrame.GetBytes(8, 7+count);
        String valueStr = null;
        try {
            valueStr = new String(valuebyte,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        Util.dtSave(CONFIGGPRS_APN_KEY, valueStr);
    }


    public static  void configGprsAccountWithRFrame(RFrame pRFrame)
    {
        byte subByte = pRFrame.GetByte(6);
        int count = pRFrame.GetByte(7);

        byte[] valuebyte = pRFrame.GetBytes(8,  7+count);
        String valueStr = null;
        try {
            valueStr = new String(valuebyte,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        Util.dtSave(CONFIGGPRS_ACCOUNT_KEY, valueStr);
    }

    public static  void configGprsPwdWithRFrame(RFrame pRFrame)
    {
        byte subByte = pRFrame.GetByte(6);
        int count = pRFrame.GetByte(7);

        byte[] valuebyte = pRFrame.GetBytes(8,  7+count);
        String valueStr = null;
        try {
            valueStr = new String(valuebyte,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        Util.dtSave(CONFIGGPRS_PWD_KEY, valueStr);
    }

    public static byte[] gprsApnBytes()
    {
        String ValueStr = (String) Util.dtGet(CONFIGGPRS_APN_KEY, CONFIGGPRS_APN_DEF);
        byte[] bytes = new byte[0];
        try {
            bytes = ValueStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static   byte[]  gprsAccountBytes()
    {
        String ValueStr = (String) Util.dtGet(CONFIGGPRS_ACCOUNT_KEY, CONFIGGPRS_ACCOUNT_DEF);
        byte[] bytes = new byte[0];
        try {
            bytes = ValueStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public  static byte[] gprsPwdBytes()
    {
        String ValueStr = (String) Util.dtGet(CONFIGGPRS_PWD_KEY, CONFIGGPRS_PWD_DEF);
        byte[] bytes = new byte[0];
        try {
            bytes = ValueStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
