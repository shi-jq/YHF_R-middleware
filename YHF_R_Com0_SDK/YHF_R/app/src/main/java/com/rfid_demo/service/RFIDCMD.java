package com.rfid_demo.service;

import android.content.Intent;

import com.rfid_demo.main.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;


public class RFIDCMD {
    // 用于设置本次关机时间，表示 2018/1/1 8:30 关机 int[] timeoff={2014,1,1,8,30};
    //用于设置下次开机时间， 表示 2018/1/1 20:00 开机 int[] timeon ={2014,1,1,20,30};
    public static void ctrllOnAndOff(int[] timeOn, int[] timeoff) {
        Intent intent = new Intent("com.mrk.setpoweronoff");
        intent.putExtra("timeon", timeOn);
        intent.putExtra("timeoff", timeoff);
        intent.putExtra("enable", true); //使能开关机功能， 设为 false,则为关闭，
        MyApplication.contxt.sendBroadcast(intent);
    }

    public static void reboot() {

        Intent intent = new Intent("reboot.zysd.now");
        MyApplication.contxt.sendBroadcast(intent);
    }

    public static void shutdown() {
        Intent intent = new Intent("shutdown.zysd.now");
        MyApplication.contxt.sendBroadcast(intent);
    }

    public static void setNetIp(String ip, String netmask, String gateway, String dns1, String dns2) {
        Intent intent = new Intent("zysj.set.etherent.mode");

        intent.putExtra("mode", "STATIC");
        //STATIC :静态模式 DHCP动态模式

        intent.putExtra("ipAddress", ip);

        intent.putExtra("gateway", gateway);

        intent.putExtra("netmask", netmask);

        intent.putExtra("dns1", dns1);

        intent.putExtra("dns2", dns2);

        MyApplication.contxt.sendBroadcast(intent);
    }

    public static String getIpAddrForInterfaces(String interfaceName){
        try {
            Enumeration<NetworkInterface> enNetworkInterface = NetworkInterface.getNetworkInterfaces(); //获取本机所有的网络接口
            while (enNetworkInterface.hasMoreElements()) {  //判断 Enumeration 对象中是否还有数据
                NetworkInterface networkInterface = enNetworkInterface.nextElement();   //获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp()) { // 判断网口是否在使用
                    continue;
                }
                if (!interfaceName.equals(networkInterface.getDisplayName())) { // 网口名称是否和需要的相同
                    continue;
                }
                Enumeration<InetAddress> enInetAddress = networkInterface.getInetAddresses();   //getInetAddresses 方法返回绑定到该网卡的所有的 IP 地址。
                while (enInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enInetAddress.nextElement();
                    if (inetAddress instanceof Inet4Address) {  //判断是否未ipv4
                        return inetAddress.getHostAddress();
                    }
//                    判断未lo时
//                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
//                        return inetAddress.getHostAddress();
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getIpAddrMaskForInterfaces(String interfaceName) {
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();    //获取本机所有的网络接口
            while (networkInterfaceEnumeration.hasMoreElements()) { //判断 Enumeration 对象中是否还有数据
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement(); //获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp() && !interfaceName.equals(networkInterface.getDisplayName())) { //判断网口是否在使用，判断是否时我们获取的网口
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {    //
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {    //仅仅处理ipv4
                        return calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength());   //获取掩码位数，通过 calcMaskByPrefixLength 转换为字符串
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getGateWay() {
        String [] arr;
        try {
            Process  process = Runtime.getRuntime().exec("ip route list table 0");
            String data = null;
            BufferedReader ie = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String string = in.readLine();

            arr = string.split("\\s+");
            return arr[2];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //通过子网掩码的位数计算子网掩码
    public static String calcMaskByPrefixLength(int length) {

        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static void setTime(Date date) {
        try {
            Intent intent = new Intent("zysj.set.system.clock");
            intent.putExtra("time", dateFormat.format(date) + " " + timeFormat.format(date));//时间
            MyApplication.contxt.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
