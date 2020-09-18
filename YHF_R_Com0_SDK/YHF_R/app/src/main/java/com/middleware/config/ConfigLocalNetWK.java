package com.middleware.config;
import android.util.Log;

import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.Tools;
import com.rfid_demo.ctrl.Util;

public class ConfigLocalNetWK  {
    public static final String PORT_KEY = "ConfigLocalNet_PORT_KEY";
    public static final String IP_KEY = "ConfigLocalNet_IP_KEY";
    public static final String IP_YANMA_KEY = "ConfigLocalNet_IP_YANMA_KEY";
    public static final String IP_WANGGUAN_KEY = "ConfigLocalNet_IP_WANGGUAN_KEY";
    public static final String ADD_TIME_KEY = "ConfigLocalNet_ADD_TIME_KEY";
    public static final String CLIENT_TYPE_KEY = "ConfigLocalNet_CLIENT_TYPE_KEY";

    public enum ClientType {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public ClientType type = ClientType.NONE;//客户端连接的类型
    public String ipAddr = "127.0.0.1";//转发的ip
    public String ipYanma = "255.255.255.0";//转发的ip
    public String ipWangGuan = "192.168.1.1";//转发的ip
    public int port = 60001;//转发的端口
    public boolean isAddTime = true;//是否加上时间戳

    public ConfigLocalNetWK() {
        this.ipAddr = (String) Util.dtGet(IP_KEY, "127.0.0.1");
        this.ipYanma = (String) Util.dtGet(IP_YANMA_KEY, "255.255.255.0");
        this.ipWangGuan = (String) Util.dtGet(IP_WANGGUAN_KEY, "192.168.1.1");
        this.port = (int) Util.dtGet(PORT_KEY, 60001);
        this.isAddTime = (boolean) Util.dtGet(ADD_TIME_KEY, true);
        this.type = (ClientType) Util.dtGet(CLIENT_TYPE_KEY, ClientType.NONE);
    }

    public static void configWithRFrame(RFrame pRFrame) {
        byte[] ipBytes = pRFrame.GetBytes(6, 9);
        String ip = Tools.HexBytes2TenStr(ipBytes, ".");
        Util.dtSave(ConfigLocalNetWK.IP_KEY, ip);
        Log.d("Req_msg congfigLocalNet ip", ip);

        byte[] yanMaBytes = pRFrame.GetBytes(10, 13);
        String yanMa = Tools.HexBytes2TenStr(yanMaBytes, ".");
        Util.dtSave(ConfigLocalNetWK.IP_YANMA_KEY, yanMa);
        Log.d("Req_msg congfigLocalNet yanMaBytes", yanMa);

        byte[] wangGuanBytes = pRFrame.GetBytes(14, 17);
        String wangGuan = Tools.HexBytes2TenStr(wangGuanBytes, ".");
        Util.dtSave(ConfigLocalNetWK.IP_WANGGUAN_KEY, wangGuan);
        Log.d("Req_msg congfigLocalNet wangGuan", wangGuan);
    }

}

