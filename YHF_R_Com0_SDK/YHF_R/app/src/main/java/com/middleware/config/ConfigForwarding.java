package com.middleware.config;

public class ConfigForwarding
{
    enum ForwardType
    {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public ForwardType type = ForwardType.NONE;//转发类型
    public String ipAddr = "127.0.0.1";//转发的ip
    public int port = 60001;//转发的端口
    public boolean isAddTime = true;//是否加上时间戳
}
