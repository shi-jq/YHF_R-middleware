package com.middleware.config;

public class ConfigReader
{
    enum LinkType
    {
        Serial,
    };

    public static LinkType linkType = LinkType.Serial;
    public static int comNum = 0;
    public static int baudrate = 115200;
}
