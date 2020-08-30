package com.middleware.config;

public class ConfigForwarding
{
    enum ForwardType
    {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public static ForwardType type = ForwardType.NONE;//转发类型
    public static String ipAddr = "127.0.0.1";//转发的ip
    public static int port = 60001;//转发的端口
    public static boolean isAddTime = true;//是否加上时间戳
}
