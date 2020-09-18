package com.middleware.config;
import com.rfid_demo.ctrl.Util;

//客户端转发的配置 连接配置
public class ConfigUpload
{
    public static final String WORK_MODE_KEY = "ConfigUpload_WORK_MODE_KEY";
    public static final String DEVICE_TYPE_KEY = "ConfigUpload_DEVICE_TYPE_KEY";
    public static final String DATA_PUSH_KEY = "ConfigUpload_DATA_PUSH_KEY";

    public  enum WorkMode{
        WorkModeDefault(0),
        WorkModeRound(0),
        WorkModeAuto(1);

        private final int value;
        WorkMode( int val){
            this.value = val;
        }
    }

    public  enum DataPush{
        DataPusAll(0),
        DataPushInvaild(1);

        private final int value;
        DataPush( int val){
            this.value = val;
        }
    }


    public WorkMode workMode =  WorkMode.WorkModeDefault;//客户端连接的类型
    public  DataPush dataPush = DataPush.DataPushInvaild;
    public ConfigUpload()
    {
        int workModenum = (int) Util.dtGet(WORK_MODE_KEY,0);
        if (workModenum == WorkMode.WorkModeAuto.value) {
            this.workMode = WorkMode.WorkModeAuto;
        }else {
            this.workMode =WorkMode.WorkModeDefault;
        }

        int dataPushNum = (int) Util.dtGet(DATA_PUSH_KEY,0);
        if (dataPushNum == DataPush.DataPusAll.value) {
            this.dataPush  = DataPush.DataPusAll;
        }else {
            this.dataPush = DataPush.DataPushInvaild;
        }

    }
}
