package com.middleware.config;

import com.rfid_demo.ctrl.Util;

public class ConfigServer {

    public enum ServerType
    {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }
    public static final String PORT_KEY = "ConfigServer_PORT_KEY";
    public static final String CLIENT_TYPE_KEY = "ConfigServer_CLIENT_TYPE_KEY";

    public ServerType type = ServerType.NONE;//服务端监听的类型
    public int port = 60001;//监听的端口

    public ConfigServer()
    {
        this.port = (int) Util.dtGet(PORT_KEY,60001);
        this.type = (ServerType) Util.dtGet(CLIENT_TYPE_KEY, ServerType.NONE);
    }
}
