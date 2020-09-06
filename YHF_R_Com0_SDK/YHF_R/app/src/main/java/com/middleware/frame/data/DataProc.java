package com.middleware.frame.data;


import com.middleware.frame.common.INT32U;
import com.middleware.frame.ctrl.RfidCommand;



public class DataProc {
    public static final int SEND_FRAME_MAXBUFF = 1024;
    private static final byte FRAME_HEAD = (byte) 170;
    private static final byte FRAME_HEAD_REPLACE = (byte) 171;
    private static final byte FRAME_FRAME_REPLACE = (byte) 172;
    private static int[] CrcTable = new int[]{
            0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7,
            0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef,
            0x1231, 0x0210, 0x3273, 0x2252, 0x52b5, 0x4294, 0x72f7, 0x62d6,
            0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de,
            0x2462, 0x3443, 0x0420, 0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485,
            0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4,
            0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df, 0xe7fe, 0xd79d, 0xc7bc,
            0x48c4, 0x58e5, 0x6886, 0x78a7, 0x0840, 0x1861, 0x2802, 0x3823,
            0xc9cc, 0xd9ed, 0xe98e, 0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b,
            0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71, 0x0a50, 0x3a33, 0x2a12,
            0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a,
            0x6ca6, 0x7c87, 0x4ce4, 0x5cc5, 0x2c22, 0x3c03, 0x0c60, 0x1c41,
            0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49,
            0x7e97, 0x6eb6, 0x5ed5, 0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70,
            0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b, 0xaf3a, 0x9f59, 0x8f78,
            0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f,
            0x1080, 0x00a1, 0x30c2, 0x20e3, 0x5004, 0x4025, 0x7046, 0x6067,
            0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e,
            0x02b1, 0x1290, 0x22f3, 0x32d2, 0x4235, 0x5214, 0x6277, 0x7256,
            0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e, 0xe54f, 0xd52c, 0xc50d,
            0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
            0xa7db, 0xb7fa, 0x8799, 0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c,
            0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657, 0x7676, 0x4615, 0x5634,
            0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab,
            0x5844, 0x4865, 0x7806, 0x6827, 0x18c0, 0x08e1, 0x3882, 0x28a3,
            0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e, 0x8bf9, 0x9bd8, 0xabbb, 0xbb9a,
            0x4a75, 0x5a54, 0x6a37, 0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92,
            0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa, 0xad8b, 0x9de8, 0x8dc9,
            0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1,
            0xef1f, 0xff3e, 0xcf5d, 0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8,
            0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0};
    private int mFrameNum = 0;
    private int mFrameLed = 0;
    private int mFrameBuzzer = 0;
    private int mFramePriority = 0;
    private int mFrameAnswer = 0;
    private byte mBusAddr = 0;
    private RFrameList mWaitFrameList  = new RFrameList();
    private RFrameList mValidFrameList  = new RFrameList();

    public void ResetFrame() {
        this.mFrameLed = 0;
        this.mFrameBuzzer = 0;
        this.mFramePriority = 0;
        this.mFrameAnswer = 0;
        this.mFrameNum = 0;
    }

    //心跳包特点,一问一答
    public boolean isHeardCommand(RfidCommand pSendCommand)
    {
        return  pSendCommand == RfidCommand.COM_SEND_HEART;
    }

    public boolean isReportCommand(byte pSendCommand)
    {
        if(pSendCommand == RfidCommand.COM_SEND_HEART.GetValue()  ||
                pSendCommand == RfidCommand.COM_YMAKE_TAGUPLOAD.GetValue()
        )
        {
            return true;
        }

        return  false;
    }
    //停止读卡命令
    public boolean isStopRead(byte pSendCommand)
    {
        return  pSendCommand == RfidCommand.COM_YSTOP.GetValue();
    }

    //开始读卡命令
    public boolean isStartRead(byte pSendCommand)
    {
        return  pSendCommand == RfidCommand.COM_YMAKE_TAGUPLOAD.GetValue();
    }

    public void updateFrameCrc(RFrame pRFrame)
    {
        int CRCValue = 0;
        CRCValue = GetFrameCrc(pRFrame, pRFrame.GetRealBuffLen());
        pRFrame.Insert((byte) (CRCValue >>> 8 & 0xFF));
        pRFrame.Insert((byte) (CRCValue & 0xFF));
    }

    public void PackMsg(byte[] pForSend, INT32U nForSendLen, RFrame pRFrame)
    {
        pForSend[0] = FRAME_HEAD;
        int index = 1;

        for (int i = 1; i < pRFrame.GetRealBuffLen(); i++) {
            index = ReplaceByte(pForSend, index, pRFrame.GetByte(i));
        }

        nForSendLen.SetValue(index);
    }

        public void PackMsg(RfidCommand pSendCommand, byte[] pSendData, int dataLength, byte[] pForSend, INT32U nForSendLen, RFrame pRFrame) {


        this.mFrameNum++;
        if (this.mFrameNum >= 16) {
            this.mFrameNum = 1;
        }

//        this.mFrameNum = 0;
//        this.mRfidSystemCfg.nBusAddr = 0;

        pRFrame.Insert(FRAME_HEAD);

        pRFrame.Insert(
                (byte) (this.mFrameLed + this.mFrameBuzzer + this.mFramePriority + this.mFrameAnswer + this.mFrameNum));
        pRFrame.Insert(this.mBusAddr);
        pRFrame.Insert((byte) (dataLength + 1));
        pRFrame.Insert(pSendCommand.GetValue());
        pRFrame.InitData(pSendData, dataLength);

        int CRCValue = 0;
        CRCValue = GetFrameCrc(pRFrame, pRFrame.GetRealBuffLen());
        pRFrame.Insert((byte) (CRCValue >>> 8 & 0xFF));
        pRFrame.Insert((byte) (CRCValue & 0xFF));

        pForSend[0] = FRAME_HEAD;
        int index = 1;

        for (int i = 1; i < pRFrame.GetRealBuffLen(); i++) {
            index = ReplaceByte(pForSend, index, pRFrame.GetByte(i));
        }

        nForSendLen.SetValue(index);
    }


    private int ReplaceByte(byte[] Buff, int insertIndex, byte insertByte) {
        if (insertByte == FRAME_HEAD) {
            Buff[insertIndex] = FRAME_HEAD_REPLACE;
            insertIndex++;
            Buff[insertIndex] = FRAME_HEAD_REPLACE;
            insertIndex++;
        } else if (insertByte == FRAME_HEAD_REPLACE) {
            Buff[insertIndex] = FRAME_HEAD_REPLACE;
            insertIndex++;
            Buff[insertIndex] = FRAME_FRAME_REPLACE;
            insertIndex++;
        } else {
            Buff[insertIndex] = insertByte;
            insertIndex++;
        }

        return insertIndex;
    }

    //如果修改了帧数据格式, 那么就需要重置下crc码
    public static void ResetFrameCrc(RFrame pRFrame)
    {
        short crc = GetFrameCrc(pRFrame);
        pRFrame.SetCrc(crc);
    }

    private static short GetFrameCrc(RFrame pRFrame)
    {
        int realLen = pRFrame.GetRealBuffLen();
        if (realLen < 5)
        {
            return (short) 0xFFFF;
        }
        return (short) GetFrameCrc(pRFrame,realLen-2);
    }

    private static int GetFrameCrc(RFrame pRFrame, int checkLen) {
        int CRCValue = 0xffff;

        for (int i = 1; i < checkLen; i++) {
            try {
                CRCValue = CountCRC16(pRFrame.GetByte(i), CRCValue);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }


        return CRCValue;
    }


//    private int  GetFrameCrc (RFrame pRFrame, int checkLen)
//    {
//        int CRCValue = 0xffff;
//
//        for (int i = checkLen; i > 0; i--) {
//            try {
//                CRCValue = CountCRC16(pRFrame.GetByte(i), CRCValue);
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//        }
//
//        return CRCValue;
//    }


    private static int CountCRC16(byte dataMsg, int crc) {
        int nCrc = 0;
        try {
            int nIndex = 0xFF & (0xFF & crc >>> 8 ^ dataMsg);
            nCrc = (crc << 8 ^ CrcTable[nIndex]) & 0xFFFF;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return nCrc;
    }


    public boolean UnPackMsg(byte[] revData, int revLength) {
        if (revLength == 0) {
            return false;
        }

        AnalysisBuff(revData, revLength);


        CheckRFrame();

        return true;
    }

    private void AnalysisBuff(byte[] revData, int revLength) {
        byte byte1 = 0;
        byte byte2 = 0;
        for (int index = 0; index < revLength; index++) {
            byte1 = revData[index];
            byte2 = 0;
            if (index + 1 != revLength) {
                byte2 = revData[index + 1];
            }

            if (byte1 == FRAME_HEAD) {
                RFrame pNewRFrame = new RFrame();
                this.mWaitFrameList.AddRFrame(pNewRFrame);
            }

            RFrame pRFrame = this.mWaitFrameList.GetLastRFrame();
            if (pRFrame != null) {
                index = ReplaceData(pRFrame, byte1, byte2, index);
            }
        }
    }


    private void CheckRFrame() {
        RFrame pRFrame = null;
        while (true) {
            pRFrame = this.mWaitFrameList.GetFirstRFrame();
            if (pRFrame == null) {
                break;
            }

            if (CheckRFrame(pRFrame)) {
                this.mValidFrameList.AddRFrame(pRFrame);
            }


            if (pRFrame.GetLength() != pRFrame.GetRealBuffLen() &&
                    this.mWaitFrameList.GetCount() == 1) {
                break;
            }

            this.mWaitFrameList.RemoveRFrame();
        }
    }


    private boolean CheckRFrame(RFrame pRFrame) {
        int buffRealLen = pRFrame.GetRealBuffLen();
        if (pRFrame.GetLength() + 6 != buffRealLen) {
            return false;
        }

        int CRCValue = 0;
        CRCValue = GetFrameCrc(pRFrame, buffRealLen);

        return CRCValue == 0;
    }


    public void GetFrameList(RFrameList framelist) {
        int count = this.mValidFrameList.GetCount();
        for (int i = 0; i < count; i++) {
            RFrame pRFrame = this.mValidFrameList.GetRFrame(i);
            framelist.AddRFrame(pRFrame);
        }
        this.mValidFrameList.ClearAll();
    }


    private int ReplaceData(RFrame pRframe, byte data1, byte data2, int index) {
        if (data1 == FRAME_HEAD_REPLACE && data2 == FRAME_HEAD_REPLACE) {
            index++;
            pRframe.Insert(FRAME_HEAD);

        } else if (data1 == FRAME_HEAD_REPLACE && data2 == FRAME_FRAME_REPLACE) {
            index++;
            pRframe.Insert(FRAME_HEAD_REPLACE);
        } else {
            pRframe.Insert(data1);
        }
        return index;
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\reader\sendwrite\DataProc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */