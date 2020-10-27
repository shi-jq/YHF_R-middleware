package com.middleware.config;

import android.util.Log;

import com.middleware.frame.common.INT16U;
import com.middleware.frame.common.INT8U;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.Tools;
import com.rfid_demo.ctrl.Util;

//客户端转发的配置 连接配置
public class ConfigClient
{
    public static final String PORT_KEY = "ConfigClient_PORT_KEY";
    public static final String IP_KEY = "ConfigClient_IP_KEY";
    public static final String CLIENT_TYPE_KEY = "ConfigClient_CLIENT_TYPE_KEY";
    public static final String SOUND_ENABLE_KEY = "ConfigClient_SOUND_ENABLE_KEY";
    public enum ClientType
    {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public ClientType type = ClientType.NONE;//客户端连接的类型
    public String ipAddr = "192.168.1.169";//转发的ip
    public int port = 60002;//转发的端口
    public int soundEnable = 1;

    public ConfigClient()
    {
      this.ipAddr = (String) Util.dtGet(IP_KEY,"192.168.1.169");
      this.port = (int) Util.dtGet(PORT_KEY,60002);
      this.type = (ClientType) Util.dtGet(CLIENT_TYPE_KEY,ClientType.NONE);
      this.soundEnable = (int) Util.dtGet(SOUND_ENABLE_KEY,1);
    }

    public static  void configSoundWithRFrame(RFrame pRFrame)
    {
        int val = pRFrame.GetBytes(8, 8)[0];
        ConfigMngr.getInstance().client.soundEnable = val;
        if(val == 1)
        {
            Util.play(1, 0, 1, 1);
        }
        Util.dtSave(SOUND_ENABLE_KEY, val);
    }

    public static  void configIPWithRFrame(RFrame pRFrame)
    {
        byte[] ipBytes = pRFrame.GetBytes(8, 11);
        String ip = Tools.HexBytes2TenStr(ipBytes, ".");
        Util.dtSave(IP_KEY, ip);
        Log.d("query_msg configClientUpload ip", ip);
    }

    public static  void configPortWithRFrame(RFrame pRFrame)
    {
        byte[] ipBytes = pRFrame.GetBytes(8, 9);
        String hexPport = Tools.HexBytesStr(ipBytes);
        Integer port = Integer.parseInt(hexPport, 16);
        Util.dtSave(ConfigClient.PORT_KEY, port);
        Log.d("Req_msg configClientUpload port", String.valueOf(port));
    }

    public  static byte[] ipBytes()
    {
        String ip = (String) Util.dtGet(IP_KEY,"192.168.1.169");
        String[] strs = ip.trim().split("\\.");
        byte[] ret = new byte[4];
        for (int i = 0; i < strs.length; i++)
        {
            int val = Integer.parseInt(strs[i]);
            ret[i] = new INT8U(val).GetValue();
        }
        return ret;
    }

    public static byte[] portBytes()
    {
        int port = (int) Util.dtGet(PORT_KEY,60001);
        INT16U b = new INT16U(port);

        return new byte[]{b.GetByte1(),b.GetByte2()};
    }

    public static byte[] soundEnableBytes()
    {
        int enable = (int) Util.dtGet(SOUND_ENABLE_KEY,1);
        return new byte[]{new INT8U(enable).GetValue()};
    }
}
