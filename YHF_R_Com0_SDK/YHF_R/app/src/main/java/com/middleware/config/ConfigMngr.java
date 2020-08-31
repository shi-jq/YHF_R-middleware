package com.middleware.config;

import java.util.LinkedList;

public class ConfigMngr
{
    //读写器相关的配置
    public static ConfigReaderSerial readerSerial = new ConfigReaderSerial();
    //与PC端连接的串口的配置
    public static ConfigPcSerial pcSerial = new ConfigPcSerial();
    //监听的端口
    public static LinkedList<ConfigServer> serverList  = new LinkedList<ConfigServer>();
    //请求的端口
    public static LinkedList<ConfigClient> clinetList  = new LinkedList<ConfigClient>();

    public void reloadConfig()
    {

    }

    //只需要一个
    public void setClient(ConfigClient foward)
    {
        clinetList.clear();
        clinetList.add(foward);
        saveToLocal();
    }

    //添加多个
    public void addClient(ConfigClient foward)
    {
        clinetList.add(foward);
        saveToLocal();
    }

    //保存到本地
    public  void saveToLocal()
    {

    }
}
