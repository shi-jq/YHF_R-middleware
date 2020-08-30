package com.middleware.config;

public class ConfigServer {

    public enum ServerType
    {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public ServerType type = ServerType.NONE;//服务端监听的类型
    public int port = 60001;//监听的端口


}
