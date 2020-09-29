package com.middleware.config;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFrame;
import com.rfid_demo.ctrl.Util;

//客户端转发的配置 连接配置
public class ConfigUpload
{
    public static final String WORK_MODE_KEY = "ConfigUpload_WORK_MODE_KEY";
    public static final String DATA_PORT_KEY = "ConfigUpload_DATA_PORT_KEY";
    public static final byte[] AutoCommand = {(byte) 0xAA,0x00, (byte) 0xCC,0x03,0x62,0x01,0x01, (byte) 0xFB,0x44};

    public enum WORK_MODE_TYPE
    {
        WORK_MODE_COMMAND(0),
        WORK_MODE_AUTO(1);

        private final int value;
        WORK_MODE_TYPE(int value) {
            this.value = value;
        }
        public int GetValue() {
            return  this.value;
        }
    };


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
        public int GetValue() {
            return  this.value;
        }
    };

    public int  workMode =  WORK_MODE_TYPE.WORK_MODE_COMMAND.GetValue();//客户端连接的类型
    public int  dataPush = PORT_TYPE.PORT_TYPE_RS232.GetValue() ;
    public RFrame autoComandFrame = null;

    public ConfigUpload()
    {
        this.workMode = (int) Util.dtGet(WORK_MODE_KEY,WORK_MODE_TYPE.WORK_MODE_COMMAND.GetValue());
        this.dataPush = (int) Util.dtGet(DATA_PORT_KEY,PORT_TYPE.PORT_TYPE_RS232.GetValue());
        autoComandFrame = new RFrame();
        autoComandFrame.InitHeadAndData(AutoCommand,AutoCommand.length);

        int busadddr = (int) Util.dtGet(ConfigPcSerial.BUS_RATE_KEY, ConfigPcSerial.BUS_ADDR_DEFAULT);
        autoComandFrame.SetBusAddr((byte) busadddr);
        DataProc.ResetFrameCrc(autoComandFrame);
    }

    public  static byte[] workModeBytes()
    {
        int model = (int) Util.dtGet(WORK_MODE_KEY,WORK_MODE_TYPE.WORK_MODE_COMMAND.GetValue());
        return new byte[]{(byte) model};
    }

    public static byte[] dataPortBytes()
    {
        int port = (int) Util.dtGet(DATA_PORT_KEY,PORT_TYPE.PORT_TYPE_RS232.GetValue());
        return new byte[]{(byte) port};
    }
}
