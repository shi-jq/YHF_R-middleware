package com.middleware.config;
import com.rfid_demo.ctrl.Util;

//客户端转发的配置 连接配置
public class ConfigUpload
{
    public static final String WORK_MODE_KEY = "ConfigUpload_WORK_MODE_KEY";
    public static final String DATA_PORT_KEY = "ConfigUpload_DATA_PORT_KEY";

    public enum PORT_TYPE
    {
        PORT_TYPE_RS232(0),
        PORT_TYPE_WEIGAND(1),
        PORT_TYPE_RS485(2),
        PORT_TYPE_TCP(3),
        PORT_TYPE_UDP(4);

        private final int value;
        PORT_TYPE(int value) {
            this.value = value;
        }
        public byte GetValue() {
            return (byte) this.value;
        }
    };

    public int  workMode =  0;//客户端连接的类型
    public int  dataPush = PORT_TYPE.PORT_TYPE_RS232.GetValue() ;

    public ConfigUpload()
    {
        this.workMode = (int) Util.dtGet(WORK_MODE_KEY,0);
        this.dataPush = (int) Util.dtGet(DATA_PORT_KEY,PORT_TYPE.PORT_TYPE_RS232.GetValue());
    }

    public  static byte[] workModeBytes()
    {
        int model = (int) Util.dtGet(WORK_MODE_KEY,0);
        return new byte[]{(byte) model};
    }

    public static byte[] dataPortBytes()
    {
        int port = (int) Util.dtGet(DATA_PORT_KEY,PORT_TYPE.PORT_TYPE_RS232.GetValue());
        return new byte[]{(byte) port};
    }
}
