package com.middleware.config;
import android.util.Log;

import com.middleware.frame.common.INT16U;
import com.middleware.frame.common.INT8U;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.Tools;
import com.rfid_demo.ctrl.Util;
import com.rfid_demo.service.RFIDCMD;

public class ConfigLocalNetWK  {
    public static final String TCP_PORT_KEY = "ConfigLocalNet_TCP_PORT_KEY";
    public static final String UDP_PORT_KEY = "ConfigLocalNet_UDP_PORT_KEY";
    public static final String IP_KEY = "ConfigLocalNet_IP_KEY";
    public static final String IP_YANMA_KEY = "ConfigLocalNet_IP_YANMA_KEY";
    public static final String IP_WANGGUAN_KEY = "ConfigLocalNet_IP_WANGGUAN_KEY";
    public static final String ADD_TIME_KEY = "ConfigLocalNet_ADD_TIME_KEY";
    public static final String CLIENT_TYPE_KEY = "ConfigLocalNet_CLIENT_TYPE_KEY";
    public static final String MAC_KEY = "ConfigLocalNet_MAC";

    public static final int TCP_PORT_DEFAULT = 7086;
    public static final int UDP_PORT_DEFAULT = 7088;
    public static final String IP_DEFAULT = "192.168.1.168";
    public static final String IP_YANMA_DEFAULT = "255.255.255.0";
    public static final String IP_WANGGUAN_DEFAULT = "192.168.1.1";
    public static final boolean ADD_TIME_DEFAULT = true;
    public static final ClientType CLIENT_TYPE_DEFAULT = ClientType.NONE;
    public static final String MAC_DEFAULT = "98-E7-F4-4E-7D-5B";
    public enum ClientType {
        NONE,//不转发,原路返回
        UDP,
        TCP
    }

    public ClientType type = CLIENT_TYPE_DEFAULT;//客户端连接的类型
    public String ipAddr = IP_DEFAULT;//转发的ip
    public String ipYanma = IP_YANMA_DEFAULT;//转发的ip
    public String ipWangGuan = IP_WANGGUAN_DEFAULT;//转发的ip
    public int tcp_port = TCP_PORT_DEFAULT;
    public int udp_port = UDP_PORT_DEFAULT;
    public boolean isAddTime = ADD_TIME_DEFAULT;//是否加上时间戳
    public String mac = MAC_DEFAULT;//转发的ip

    public ConfigLocalNetWK() {

        this.tcp_port = (int) Util.dtGet(TCP_PORT_KEY, TCP_PORT_DEFAULT);
        this.udp_port = (int) Util.dtGet(UDP_PORT_KEY, UDP_PORT_DEFAULT);
        this.isAddTime = (boolean) Util.dtGet(ADD_TIME_KEY, ADD_TIME_DEFAULT);
        this.type = (ClientType) Util.dtGet(CLIENT_TYPE_KEY, CLIENT_TYPE_DEFAULT);
        this.mac = (String) Util.dtGet(MAC_KEY, MAC_DEFAULT);

        this.ipAddr= (String) Util.dtGet(IP_KEY, IP_DEFAULT);
        this.ipYanma = (String) Util.dtGet(IP_YANMA_KEY, IP_YANMA_DEFAULT);
        this.ipWangGuan = (String) Util.dtGet(IP_WANGGUAN_KEY, IP_WANGGUAN_DEFAULT);
        String ipAddr_Real  = RFIDCMD.getIpAddrForInterfaces("eth0");
        String ipYanma_Real = RFIDCMD.getIpAddrMaskForInterfaces("eth0");
        String ipWangGuan_Real = RFIDCMD.getGateWay();

        if (ipAddr_Real != ipAddr || ipWangGuan_Real != ipWangGuan)
        {
            RFIDCMD.setNetIp(ipAddr,ipYanma,ipWangGuan,ipWangGuan,"0.0.0.0");
        }
    }

    public static boolean configWithRFrame(RFrame pRFrame) {

        boolean bRet = true;
        byte subByte = pRFrame.GetByte(5);
        int count = pRFrame.GetDataLength();

        if (subByte == 0x01 && count >= 6) {
            byte[] ipBytes = pRFrame.GetBytes(6, 11);
            String ip = Tools.HexBytes216Str(ipBytes, "-");
            Util.dtSave(MAC_KEY, ip);
            Log.d("Req_msg congfigLocalNet mac", ip);
        }
        else if (subByte == 0x02 && count >= 12) {
            byte[] ipBytes = pRFrame.GetBytes(6, 9);
            String ip = Tools.HexBytes2TenStr(ipBytes, ".");
            Util.dtSave(IP_KEY, ip);
            Log.d("Req_msg congfigLocalNet ip", ip);

            byte[] yanMaBytes = pRFrame.GetBytes(10, 13);
            String yanMa = Tools.HexBytes2TenStr(yanMaBytes, ".");
            Util.dtSave(IP_YANMA_KEY, yanMa);
            Log.d("Req_msg congfigLocalNet yanMaBytes", yanMa);

            byte[] wangGuanBytes = pRFrame.GetBytes(14, 17);
            String wangGuan = Tools.HexBytes2TenStr(wangGuanBytes, ".");
            Util.dtSave(IP_WANGGUAN_KEY, wangGuan);
            Log.d("Req_msg congfigLocalNet wangGuan", wangGuan);
        }
        else if (subByte == 0x03 && count >= 2) {
            byte[] port = pRFrame.GetBytes(6, 9);
            INT16U n16u = new INT16U(0);
            n16u.SetValue(port[0],port[1]);
            Util.dtSave(TCP_PORT_KEY, n16u.GetValue());
        }
        else if (subByte == 0x04 && count >= 2) {
            byte[] port = pRFrame.GetBytes(6, 9);
            INT16U n16u = new INT16U(0);
            n16u.SetValue(port[0],port[1]);
            Util.dtSave(UDP_PORT_KEY, n16u.GetValue());
        }
        else{
            bRet = false;
        }

        return  bRet;
    }


    public  static byte[] tcpPortBytes()
    {
        byte[] ret = new byte[2];
        int tcp_port = (int) Util.dtGet(TCP_PORT_KEY, TCP_PORT_DEFAULT);
        INT16U n16u = new INT16U(tcp_port);
        ret[0] = n16u.GetByte1();
        ret[1] = n16u.GetByte2();
        return ret;
    }

    public  static byte[] udpPortBytes()
    {
        byte[] ret = new byte[2];
        int tcp_port = (int) Util.dtGet(UDP_PORT_KEY, UDP_PORT_DEFAULT);
        INT16U n16u = new INT16U(tcp_port);
        ret[0] = n16u.GetByte1();
        ret[1] = n16u.GetByte2();
        return ret;
    }

    public  static byte[] macBytes()
    {
        byte[] ret = new byte[6];
        try {
            String str = (String) Util.dtGet(MAC_KEY, MAC_DEFAULT);
            String[] strs = str.trim().split("\\-");
            for (int i = 0; i < strs.length; i++)
            {
                int val = Integer.parseInt(strs[i],16);
                ret[i] = new INT8U(val).GetValue();
            }
        }
        catch (Exception e)
        {
            ret[0] = 0x00;
            ret[1] = 0x00;
            ret[2] = 0x00;
            ret[3] = 0x00;
            ret[4] = 0x00;
            ret[5] = 0x00;

        }

        return ret;
    }

    public  static byte[] ipBytes()
    {
        String str = (String) Util.dtGet(IP_KEY,IP_DEFAULT);
        String[] strs = str.trim().split("\\.");
        byte[] ret = new byte[12];
        for (int i = 0; i < strs.length; i++)
        {
            int val = Integer.parseInt(strs[i]);
            ret[i] = new INT8U(val).GetValue();
        }

        str = (String) Util.dtGet(IP_YANMA_KEY, IP_YANMA_DEFAULT);
        strs = str.trim().split("\\.");
        for (int i = 0; i < strs.length; i++)
        {
            int val = Integer.parseInt(strs[i]);
            ret[i+4] = new INT8U(val).GetValue();
        }

        str = (String) Util.dtGet(IP_WANGGUAN_KEY, IP_WANGGUAN_DEFAULT);
        strs = str.trim().split("\\.");
        for (int i = 0; i < strs.length; i++)
        {
            int val = Integer.parseInt(strs[i]);
            ret[i+8] = new INT8U(val).GetValue();
        }
        return ret;
    }


}

