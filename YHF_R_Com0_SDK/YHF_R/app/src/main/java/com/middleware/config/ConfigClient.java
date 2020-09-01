package com.middleware.config;

//客户端转发的配置 连接配置
public class ConfigClient
{
    public enum ClientType
    {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public ClientType type = ClientType.NONE;//客户端连接的类型
    public String ipAddr = "127.0.0.1";//转发的ip
    public int port = 60001;//转发的端口
    public boolean isAddTime = true;//是否加上时间戳
}
