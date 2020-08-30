package com.middleware.config;

public class ConfigForwarding
{
    enum ForwardType
    {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public static ForwardType type = ForwardType.NONE;
    public static boolean isEnable = false;
    public static String ipAddr = "127.0.0.1";
    public static int port = 60001;
}
