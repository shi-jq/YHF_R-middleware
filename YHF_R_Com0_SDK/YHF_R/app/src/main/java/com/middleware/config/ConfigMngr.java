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

        if (congfigLocalNet(byteCommand, pRFrame) == RFrame.SuccessHandler){
            canHander = RFrame.SuccessHandler;;
        }


        if (configClientUpload(byteCommand, pRFrame) == RFrame.SuccessHandler){
            canHander = RFrame.SuccessHandler;;
        }

        return  canHander;
    }


    private static  int congfigLocalNet(byte byteCommand,RFrame pRFrame)
    {
        int canHander = RFrame.FailHandler;

        //设置网口相关
        if (DataProc.isConfingNetwork(byteCommand))
        {
            byte subByte = pRFrame.GetByte(5);
            int count = pRFrame.GetRealBuffLen();

            if (subByte == 0x02 && count >= 20)
            {
                byte[] ipBytes = pRFrame.GetBytes(6,9);
                String ip =   Tools.HexBytes2TenStr(ipBytes,".");
                saveVal(ConfigLocalNet.IP_KEY, ip);
                Log.d("Req_msg congfigLocalNet ip", ip);

                byte[] yanMaBytes = pRFrame.GetBytes(10,13);
                String yanMa =   Tools.HexBytes2TenStr(yanMaBytes,".");
                saveVal(ConfigLocalNet.IP_YANMA_KEY, yanMa);
                Log.d("Req_msg congfigLocalNet yanMaBytes", yanMa);

                byte[] wangGuanBytes = pRFrame.GetBytes(14,17);
                String wangGuan =   Tools.HexBytes2TenStr(wangGuanBytes,".");
               saveVal(ConfigLocalNet.IP_WANGGUAN_KEY, wangGuan);
                Log.d("Req_msg congfigLocalNet wangGuan", wangGuan);

                canHander = RFrame.SuccessHandler;;
            }
        }

        return  canHander;
    }

    //设置上传网络相关
    private static  int configClientUpload(byte byteCommand,RFrame pRFrame)
    {
        int canHander = RFrame.FailHandler;

        //设置上传相关
        if (DataProc.isConfingUpload(byteCommand))
        {
            byte subByte = pRFrame.GetByte(5);
            byte subByte2 = pRFrame.GetByte(6);
            int count = pRFrame.GetRealBuffLen();

            Log.i("Req_count", String.valueOf(count));

            //设置上传网络
            if (subByte == 0x01  && subByte2 == -6 && count >= 14)//subByte2 = FA
            {
                byte[] ipBytes = pRFrame.GetBytes(8,11);
                String ip =   Tools.HexBytes2TenStr(ipBytes,".");
                saveVal(ConfigClient.IP_KEY, ip);
                Log.d("Req_msg configClientUpload ip", ip);

                canHander = RFrame.SuccessHandler;;
            }

            //设置上传网络
            if (subByte == 0x01 && subByte2 == -2 && count >= 12)//subByte2 = FE
            {
                byte[] ipBytes = pRFrame.GetBytes(8,9);
                String hexPport =   Tools.HexBytesStr(ipBytes);
                Integer port = Integer.parseInt(hexPport,16);
                saveVal(ConfigClient.PORT_KEY, port);
                Log.d("Req_msg configClientUpload port", String.valueOf(port));
                canHander = RFrame.SuccessHandler;;
            }

            if (subByte == 0x01  && count >= 11)
            {
                byte[] ipBytes = pRFrame.GetBytes(8,8);
                String hexPport =   Tools.HexBytesStr(ipBytes);
                Integer val = Integer.parseInt(hexPport,16);

                //设置工作模式
                if (subByte2 == 00)
                {
                    saveVal(ConfigUpload.WORK_MODE_KEY, val);
                    Log.d("Req_msg configClientUpload port", String.valueOf(val));
                    canHander = RFrame.SuccessHandler;;
                }

                //设置设备类型
                if (subByte2 == 01)
                {
                    saveVal(ConfigUpload.DEVICE_TYPE_KEY, val);
                    Log.d("Req_msg configClientUpload port", String.valueOf(val));
                    canHander = RFrame.SuccessHandler;;
                }

                //设置数据是否上传
                if (subByte2 == 01)
                {
                    saveVal(ConfigUpload.DATA_PUSH_KEY, val);
                    canHander = RFrame.SuccessHandler;;
                }

                Log.d("Req_msg configClientUpload port", String.valueOf(val));
            }
        }

        return  canHander;
    }

    public static void saveVal(String saveKey,Object obj)
    {
//        Util.dtSave(saveKey,obj);
    }
}
