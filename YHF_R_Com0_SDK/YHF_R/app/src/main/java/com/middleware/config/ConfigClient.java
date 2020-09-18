package com.middleware.config;

import android.util.Log;

import com.middleware.frame.common.INT16U;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.Tools;
import com.rfid_demo.ctrl.Util;

//客户端转发的配置 连接配置
public class ConfigClient
{
    public static final String PORT_KEY = "ConfigClient_PORT_KEY";
    public static final String IP_KEY = "ConfigClient_IP_KEY";
    public static final String CLIENT_TYPE_KEY = "ConfigClient_CLIENT_TYPE_KEY";

    public enum ClientType
    {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public ClientType type = ClientType.NONE;//客户端连接的类型
    public String ipAddr = "127.0.0.1";//转发的ip
    public int port = 60001;//转发的端口

    public ConfigClient()
    {
      this.ipAddr = (String) Util.dtGet(IP_KEY,"127.0.0.1");
      this.port = (int) Util.dtGet(PORT_KEY,60001);
      this.type = (ClientType) Util.dtGet(CLIENT_TYPE_KEY,ClientType.NONE);
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
        String ip = (String) Util.dtGet(IP_KEY,"127.0.0.1");
        String[] strs = ip.trim().split(".");
        byte[] ret = new byte[4];
        for (int i = 0; i < strs.length; i++)
        {
            ret[i] = (byte) Integer.parseInt(strs[i],16);
        }
        return ret;
    }

    public static byte[] portBytes()
    {
        int port = (int) Util.dtGet(PORT_KEY,60001);
        INT16U b = new INT16U(port);

        return new byte[]{b.GetByte1(),b.GetByte2()};
    }
}
