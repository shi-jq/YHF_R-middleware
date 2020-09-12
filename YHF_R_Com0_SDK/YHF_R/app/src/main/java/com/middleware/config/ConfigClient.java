package com.middleware.config;

import com.rfid_demo.ctrl.Util;

//客户端转发的配置 连接配置
public class ConfigClient
{
    private static final String PORT_KEY = "ConfigClient_PORT_KEY";
    private static final String IP_KEY = "ConfigClient_IP_KEY";
    private static final String ADD_TIME_KEY = "ConfigClient_ADD_TIME_KEY";
    private static final String CLIENT_TYPE_KEY = "ConfigClient_CLIENT_TYPE_KEY";

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

    public ConfigClient()
    {
      this.ipAddr = (String) Util.dtGet(IP_KEY,"127.0.0.1");
      this.port = (int) Util.dtGet(PORT_KEY,60001);
      this.isAddTime = (boolean) Util.dtGet(ADD_TIME_KEY,true);
      this.type = (ClientType) Util.dtGet(CLIENT_TYPE_KEY,ClientType.NONE);
    }
}
