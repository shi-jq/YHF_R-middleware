package com.middleware.config;
import com.middleware.frame.common.INT16U;
import com.middleware.frame.data.RFrame;
import com.rfid_demo.ctrl.Util;

//客户端转发的配置 连接配置
public class ConfigUpload
{
    public static final String WORK_MODE_KEY = "ConfigUpload_WORK_MODE_KEY";
    public static final String DATA_PORT_KEY = "ConfigUpload_DATA_PORT_KEY";

    public int  workMode =  0;//客户端连接的类型
    public int  dataPush = 0;
    public ConfigUpload()
    {
        this.workMode = (int) Util.dtGet(WORK_MODE_KEY,0);
        this.dataPush = (int) Util.dtGet(DATA_PORT_KEY,0);
    }

    public  static byte[] workModeBytes()
    {
        int model = (int) Util.dtGet(WORK_MODE_KEY,0);
        return new byte[]{(byte) model};
    }

    public static byte[] dataPortBytes()
    {
        int port = (int) Util.dtGet(WORK_MODE_KEY,0);
        return new byte[]{(byte) port};
    }
}
