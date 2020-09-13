package com.middleware.config;


import com.rfid_demo.ctrl.Util;

public class ConfigMngr
{
    private static ConfigMngr configMngr = null;

   public ConfigClient client = null;
   public ConfigPcSerial pcSerial = null;
   public ConfigReaderSerial readerSerial = null;
   public ConfigServer server = null;


    public static ConfigMngr getInstance() {
        if (null == configMngr) {
            synchronized (ConfigMngr.class) {
                if (null == configMngr) {
                    configMngr = new ConfigMngr();
                }
            }
        }
        return configMngr;
    }

    private ConfigMngr()
    {
        this.client = new ConfigClient();
        this.pcSerial = new ConfigPcSerial();
        this.readerSerial = new ConfigReaderSerial();
        this.server = new ConfigServer();
    }

    public static void saveVal(String saveKey,Object obj)
    {
        Util.dtSave(saveKey,obj);
    }
}
