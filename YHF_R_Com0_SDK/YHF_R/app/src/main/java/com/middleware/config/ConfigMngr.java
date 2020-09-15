package com.middleware.config;


import android.util.Log;

import com.middleware.frame.ctrl.RfidCommand;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.Tools;
import com.rfid_demo.ctrl.Util;

public class ConfigMngr
{
   private static ConfigMngr configMngr = null;

   public ConfigClient client = null;
   public ConfigPcSerial pcSerial = null;
   public ConfigReaderSerial readerSerial = null;
   public ConfigServer server = null;


    public static ConfigMngr getInstance() {
        if (null == configMngr) {
            synchronized (ConfigMngr.class) {
                if (null == configMngr) {
                    configMngr = new ConfigMngr();
                }
            }
        }
        return configMngr;
    }

    private ConfigMngr()
    {
        this.client = new ConfigClient();
        this.pcSerial = new ConfigPcSerial();
        this.readerSerial = new ConfigReaderSerial();
        this.server = new ConfigServer();
    }


    public static  int isConfigRFrame(RFrame pRFrame)
    {
        int canHander = RFrame.FailHandler;
        byte byteCommand = pRFrame.GetRfidCommand();
        byte[] bytes = {byteCommand};
        Log.i("Req", Tools.Bytes2HexString(bytes,1));
        if (DataProc.isRestartApp(byteCommand))
        {
           canHander = RFrame.SuccessHandler;
//           Util.restartApp();
        }

        //设置网口相关
        if (DataProc.isConfingNetwork(byteCommand))
        {
            byte subByte = pRFrame.GetByte(5);
            byte[] subBytes = {byteCommand};
            int count = pRFrame.GetRealBuffLen();

            Log.i("Req_count", String.valueOf(count));

            if (subByte == 0x02 && count >= 20)
            {
                byte[] ipBytes = pRFrame.GetBytes(6,9);
                String ip =   Tools.HexBytes2TenStr(ipBytes,".");
//                saveVal(ConfigClient.IP_KEY, ip);
                Log.d("Req_msg ip", ip);

                byte[] yanMaBytes = pRFrame.GetBytes(10,13);
                String yanMa =   Tools.HexBytes2TenStr(yanMaBytes,".");
//                saveVal(ConfigClient.IP_YANMA_KEY, yanMa);
                Log.d("Req_msg yanMaBytes", yanMa);

                byte[] wangGuanBytes = pRFrame.GetBytes(14,17);
                String wangGuan =   Tools.HexBytes2TenStr(wangGuanBytes,".");
//                saveVal(ConfigClient.IP_WANGGUAN_KEY, wangGuan);
                Log.d("Req_msg wangGuan", wangGuan);

                canHander = RFrame.SuccessHandler;;
            }
        }

        //设置上传相关
        if (DataProc.isConfingUpload(byteCommand))
        {
            byte subByte = pRFrame.GetByte(5);
            byte[] subBytes = {byteCommand};
            int count = pRFrame.GetRealBuffLen();

            Log.i("Req_count", String.valueOf(count));

            //设置上传网络
            if (subByte == 0x01 && count >= 14)
            {
                byte[] ipBytes = pRFrame.GetBytes(8,11);
                String ip =   Tools.HexBytes2TenStr(ipBytes,".");
                saveVal(ConfigClient.IP_KEY, ip);
                Log.d("Req_msg ip", ip);
            }

            canHander = RFrame.SuccessHandler;;
        }

        return  canHander;
    }


    public static void saveVal(String saveKey,Object obj)
    {
        Util.dtSave(saveKey,obj);
    }
}
