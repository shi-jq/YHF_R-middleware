package com.middleware.config;

import java.util.LinkedList;

public class ConfigMngr
{
    public static ConfigReader readerConfig;
    public static LinkedList<ConfigForwarding> rowardList  = new LinkedList<ConfigForwarding>();

    void loadLocalConfig()
    {

    }

    //只需要一个
    void setConfigForwarding(ConfigForwarding foward)
    {
        rowardList.clear();
        rowardList.add(foward);
        saveToLocal();
    }

    //添加多个
    void addConfigForwarding(ConfigForwarding foward)
    {
        rowardList.add(foward);
        saveToLocal();
    }

    void saveToLocal()
    {

    }
}
