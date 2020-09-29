package com.middleware.config;

import android.util.Log;

import com.middleware.frame.common.INT8U;
import com.middleware.frame.ctrl.RfidCommand;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.Tools;
import com.middleware.request.RequestMngr;
import com.middleware.request.RequestModel;
import com.rfid_demo.ctrl.Util;
import com.rfid_demo.service.RFIDCMD;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ConfigMngr {
    private static ConfigMngr configMngr = null;

    public ConfigClient client = null;
    public ConfigPcSerial pcSerial = null;
    public ConfigReaderSerial readerSerial = null;
    public ConfigLocalNetWK server = null;
    public ConfigUpload upload = null;

    public static RFIDFrame responseRFIDFrame;
    public static RequestModel reqModel;

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

    private ConfigMngr() {
        this.client = new ConfigClient();
        this.pcSerial = new ConfigPcSerial();
        this.readerSerial = new ConfigReaderSerial();
        this.server = new ConfigLocalNetWK();
        this.upload = new ConfigUpload();
    }

    public static int canHandlerReqModel(RequestModel model) {
        reqModel = model;
        int canHander = RequestModel.FailHandler;
        RFrame pRFrame = model.pFrame;
        byte byteCommand = pRFrame.GetRfidCommand();
        byte[] bytes = {byteCommand};
        Log.i("Req", Tools.Bytes2HexString(bytes, 1));
        if (DataProc.isRestartApp(byteCommand)) {
            canHander = RequestModel.SuccessHandler;
            RequestMngr.getInstance().sendToPC(model.settingResFrame(), model.type);
            try {
                Thread.sleep(1000);
                RFIDCMD.reboot();
            } catch (Exception e) {

            }
        }

        if (congfigLocalNet(byteCommand, pRFrame) == RequestModel.SuccessHandler) {
            canHander = RequestModel.SuccessHandler;
        } else if (configClientUpload(byteCommand, pRFrame) == RequestModel.SuccessHandler) {
            canHander = RequestModel.SuccessHandler;
        }

        if (configPCSerial(byteCommand, pRFrame) == RequestModel.SuccessHandler) {
            canHander = RequestModel.SuccessHandler;
        }

        //查询设置时间
        if (canHander == RequestModel.FailHandler) {
            if (configOrQueryDate(byteCommand, pRFrame) == RequestModel.SuccessHandler) {
                canHander = RequestModel.SuccessHandler;
            }
        }

        if (canHander == RequestModel.FailHandler) {
            if (queryLocalNetWK(byteCommand, pRFrame) == RequestModel.SuccessHandler) {
                canHander = RequestModel.SuccessHandler;
            }

            if (queryLocalNet(byteCommand, pRFrame) == RequestModel.SuccessHandler) {
                canHander = RequestModel.SuccessHandler;
            }

            if (queryClientUpload(byteCommand, pRFrame) == RequestModel.SuccessHandler) {
                canHander = RequestModel.SuccessHandler;
            }
        }

        //提前处理设置的响应
        if (canHander == RequestModel.SuccessHandler &&  responseRFIDFrame == null) {
            responseRFIDFrame = model.settingResFrame();
        }

        if (responseRFIDFrame != null) {
            try {
                RequestMngr.getInstance().sendToPC(responseRFIDFrame, model.type);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                responseRFIDFrame = null;
                reqModel = null;
            }
        }

        return canHander;
    }


    private static int congfigLocalNet(byte byteCommand, RFrame pRFrame) {
        int canHander = RequestModel.FailHandler;

        //设置网口相关
        if (byteCommand == RfidCommand.COM_NETPARA_SET.GetValue()) {

            if (ConfigLocalNetWK.configWithRFrame(pRFrame)) {
                responseRFIDFrame = reqModel.resFrame((byte) 0x00);
                canHander = RequestModel.SuccessHandler;
            } else {
                responseRFIDFrame = reqModel.resFrame((byte) 0x01);
                canHander = RequestModel.SuccessHandler;
            }
        }
        return canHander;
    }

    private static int configPCSerial(byte byteCommand, RFrame pRFrame) {
        int canHander = RequestModel.FailHandler;
        if (byteCommand == RfidCommand.COM_COMMUNI_SET.GetValue()) {
            if (ConfigPcSerial.configWithRFrame(pRFrame)) {
                responseRFIDFrame = reqModel.resFrame((byte) 0x00);
            } else {
                responseRFIDFrame = reqModel.resFrame((byte) 0x01);
            }
            canHander = RequestModel.SuccessHandler;
        } else if (byteCommand == RfidCommand.COM_COMMUNI_QUERY.GetValue()) {
            responseRFIDFrame = reqModel.queryResFrame(ConfigPcSerial.baudRateBytes());
            canHander = RequestModel.SuccessHandler;
        }
        return canHander;
    }

    //设置上传网络相关
    private static int configClientUpload(byte byteCommand, RFrame pRFrame) {
        int canHander = RequestModel.FailHandler;

        //设置上传相关
        if (DataProc.isConfingUpload(byteCommand)) {
            byte subByte = pRFrame.GetByte(5);
            byte subByte2 = pRFrame.GetByte(6);
            int count = pRFrame.GetRealBuffLen();

            Log.i("Req_count", String.valueOf(count));

            //设置上传网络
            if (subByte == 0x01 && subByte2 == -6 && count >= 14)//subByte2 = FA
            {
                ConfigClient.configIPWithRFrame(pRFrame);
                canHander = RequestModel.SuccessHandler;
            }

            //设置上传网络
            if (subByte == 0x01 && subByte2 == -2 && count >= 12)//subByte2 = FE
            {
                ConfigClient.configPortWithRFrame(pRFrame);
                canHander = RequestModel.SuccessHandler;
            }

            if (subByte == 0x01 && count >= 11) {
                byte[] ipBytes = pRFrame.GetBytes(8, 8);
                String hexPport = Tools.HexBytesStr(ipBytes);
                Integer val = Integer.parseInt(hexPport, 16);

                //设置工作模式
                if (subByte2 == 56) {
                    saveVal(ConfigUpload.WORK_MODE_KEY, val);
                    canHander = RequestModel.SuccessHandler;
                }

                //设置数据是否上传
                if (subByte2 == 63) {//F1
                    saveVal(ConfigUpload.DATA_PORT_KEY, val);
                    canHander = RequestModel.SuccessHandler;
                }

                Log.d("Req_msg configClientUpload", String.valueOf(val));
            }
        }

        return canHander;
    }

    //查询或者设置时间
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static int configOrQueryDate(byte byteCommand, RFrame pRFrame) {
        int canHander = RequestModel.FailHandler;
        if (byteCommand == RfidCommand.COM_DATE_SET.GetValue()) {
            int count = pRFrame.GetRealBuffLen();
            if (count >= 14) {
                String secondStr = String.format("%02x",pRFrame.GetBytes(5, 5)[0]);
                int second = Integer.parseInt(secondStr);

                String minuteStr = String.format("%02x",pRFrame.GetBytes(6, 6)[0]);
                int minute = Integer.parseInt(minuteStr);

                String hourStr = String.format("%02x",pRFrame.GetBytes(7, 7)[0]);
                int hour = Integer.parseInt(hourStr);

                String dayOfWeekStr = String.format("%02x",pRFrame.GetBytes(8, 8)[0]);
                int dayOfWeek =Integer.parseInt( dayOfWeekStr);

                String dayStr = String.format("%02x",pRFrame.GetBytes(9, 9)[0]);
                int day = Integer.parseInt(dayStr);

                String monthStr = String.format("%02x",pRFrame.GetBytes(10, 10)[0]);
                int month = Integer.parseInt(monthStr);

                String yearStr = String.format("%02x",pRFrame.GetBytes(11, 11)[0]);
                int year = Integer.parseInt(yearStr);

                String dateStr = String.format("20%02d", year) + "-" +
                        String.format("%02d", month) + "-" +
                        String.format("%02d", day) + " " +
                        String.format("%02d", hour) + ":" +
                        String.format("%02d", minute) + ":" +
                        String.format("%02d", second);
                Date date = null;
                try {
                    date = sdf.parse(dateStr);
                    RFIDCMD.setTime(date);
                    canHander = RequestModel.SuccessHandler;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if (byteCommand == RfidCommand.COM_DATE_QUERY.GetValue()) {
            int count = pRFrame.GetRealBuffLen();
            if (count >= 7) {
                Calendar now = Calendar.getInstance();

                byte[] dateBytes = new byte[7];

                byte second = Util.tenShow16StrByte(now.get(Calendar.SECOND));
                dateBytes[0] = second;

                byte minute = Util.tenShow16StrByte(now.get(Calendar.MINUTE));
                dateBytes[1] = minute;

                byte hour = Util.tenShow16StrByte(now.get(Calendar.HOUR));
                dateBytes[2] = hour;

                byte dayOfWeek = Util.tenShow16StrByte(now.get(Calendar.DAY_OF_WEEK));
                dateBytes[3] = dayOfWeek;

                byte day = Util.tenShow16StrByte(now.get(Calendar.DAY_OF_MONTH));
                dateBytes[4] = day;

                byte month = Util.tenShow16StrByte(now.get(Calendar.MONTH) + 1);
                dateBytes[5] = month;

                byte year = Util.tenShow16StrByte(now.get(Calendar.YEAR)%100);
                dateBytes[6] = year;

                responseRFIDFrame = reqModel.queryResFrame(dateBytes);
                canHander = RequestModel.SuccessHandler;
            }
        }

        return canHander;
    }


    //查询相关
    private static int queryLocalNet(byte byteCommand, RFrame pRFrame) {
        int canHander = RequestModel.FailHandler;

        //设置网口相关
        if (DataProc.isConfingNetwork(byteCommand)) {
            byte subByte = pRFrame.GetByte(5);
            int count = pRFrame.GetRealBuffLen();

            if (subByte == 0x02 && count >= 20) {
                byte[] ipBytes = pRFrame.GetBytes(6, 9);
                String ip = Tools.HexBytes2TenStr(ipBytes, ".");
                saveVal(ConfigLocalNetWK.IP_KEY, ip);
                Log.d("Req_msg congfigLocalNet ip", ip);

                byte[] yanMaBytes = pRFrame.GetBytes(10, 13);
                String yanMa = Tools.HexBytes2TenStr(yanMaBytes, ".");
                saveVal(ConfigLocalNetWK.IP_YANMA_KEY, yanMa);
                Log.d("Req_msg congfigLocalNet yanMaBytes", yanMa);

                byte[] wangGuanBytes = pRFrame.GetBytes(14, 17);
                String wangGuan = Tools.HexBytes2TenStr(wangGuanBytes, ".");
                saveVal(ConfigLocalNetWK.IP_WANGGUAN_KEY, wangGuan);
                Log.d("Req_msg congfigLocalNet wangGuan", wangGuan);

                canHander = RequestModel.SuccessHandler;
            }
        }


        return canHander;
    }

    private static int setLocalNetWK(byte byteCommand, RFrame pRFrame) {
        int canHander = RequestModel.FailHandler;
        if (byteCommand == RfidCommand.COM_NETPARA_SET.GetValue()) {
            byte subByte = pRFrame.GetByte(5);
            if (subByte == 0x01) {
                responseRFIDFrame = reqModel.queryResFrame(ConfigLocalNetWK.macBytes());
                canHander = RequestModel.SuccessHandler;
            } else if (subByte == 0x02) {
                responseRFIDFrame = reqModel.queryResFrame(ConfigLocalNetWK.ipBytes());
                canHander = RequestModel.SuccessHandler;
            } else if (subByte == 0x03) {
                responseRFIDFrame = reqModel.queryResFrame(ConfigLocalNetWK.tcpPortBytes());
                canHander = RequestModel.SuccessHandler;
            } else if (subByte == 0x04) {
                responseRFIDFrame = reqModel.queryResFrame(ConfigLocalNetWK.udpPortBytes());
                canHander = RequestModel.SuccessHandler;
            } else {
                responseRFIDFrame = reqModel.resFrame((byte) 0x01);
                canHander = RequestModel.SuccessHandler;
            }
        }

        return canHander;
    }

    private static int queryLocalNetWK(byte byteCommand, RFrame pRFrame) {
        int canHander = RequestModel.FailHandler;
        if (byteCommand == RfidCommand.COM_NETPARA_QUERY.GetValue()) {
            byte subByte = pRFrame.GetByte(5);
            if (subByte == 0x01) {
                responseRFIDFrame = reqModel.queryResFrame(ConfigLocalNetWK.macBytes());
                canHander = RequestModel.SuccessHandler;
            } else if (subByte == 0x02) {
                responseRFIDFrame = reqModel.queryResFrame(ConfigLocalNetWK.ipBytes());
                canHander = RequestModel.SuccessHandler;
            } else if (subByte == 0x03) {
                responseRFIDFrame = reqModel.queryResFrame(ConfigLocalNetWK.tcpPortBytes());
                canHander = RequestModel.SuccessHandler;
            } else if (subByte == 0x04) {
                responseRFIDFrame = reqModel.queryResFrame(ConfigLocalNetWK.udpPortBytes());
                canHander = RequestModel.SuccessHandler;
            } else {
                responseRFIDFrame = reqModel.resFrame((byte) 0x01);
                canHander = RequestModel.SuccessHandler;
            }
        }

        return canHander;
    }

    //查询上传网络相关
    private static int queryClientUpload(byte byteCommand, RFrame pRFrame) {
        int canHander = RequestModel.FailHandler;
        //设置上传相关
        if (DataProc.isConfingUpload(byteCommand)) {
            byte subByte = pRFrame.GetByte(5);
            byte subByte2 = pRFrame.GetByte(6);
            int count = pRFrame.GetRealBuffLen();

            Log.i("subByte", String.valueOf(subByte));
            Log.i("subByte2", String.valueOf(subByte2));
            Log.i("Req_count", String.valueOf(count));

            if (subByte == 0x02) {

                //设置上传网络
                if (subByte2 == -6 && count >= 10) {
                    responseRFIDFrame = reqModel.queryResFrame(ConfigClient.ipBytes());
                    canHander = RequestModel.SuccessHandler;
                }

                //设置上传网络
                if (subByte2 == -2 && count >= 10)//subByte2 = FE
                {
                    responseRFIDFrame = reqModel.queryResFrame(ConfigClient.portBytes());
                    canHander = RequestModel.SuccessHandler;
                }

                if (count >= 10) {
                    byte[] ipBytes = pRFrame.GetBytes(8, 8);
                    String hexPport = Tools.HexBytesStr(ipBytes);
                    Integer val = Integer.parseInt(hexPport, 16);

                    //设置工作模式
                    if (subByte2 == 56) {
                        responseRFIDFrame = reqModel.queryResFrame(ConfigUpload.workModeBytes());
                        canHander = RequestModel.SuccessHandler;
                    }

                    //设置数据是否上传
                    if (subByte2 == 63) {//F1
                        responseRFIDFrame = reqModel.queryResFrame(ConfigUpload.dataPortBytes());
                        canHander = RequestModel.SuccessHandler;
                    }

                    Log.d("query_msg configClientUpload", String.valueOf(val));
                }
            }
        }

        return canHander;
    }

    public static void saveVal(String key, Object obj) {
        Util.dtSave(key, obj);
    }

    public static Object getValue(String key, Object defObj) {
        return Util.dtGet(key, defObj);
    }
}
