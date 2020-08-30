package com.middleware.config;

public class ConfigReader
{
    enum LinkType
    {
        Serial,
    };

    public LinkType linkType = LinkType.Serial;
    public int comNum = 0;
    public int baudrate = 115200;
}
