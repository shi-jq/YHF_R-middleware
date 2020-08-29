package com.rfid_demo.ctrl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;

import com.example.uart.R;
import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.INT8U;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;


public class ReadCardCtrl {
    public final static int MSG_INV_DISPLAY = 12;
    public final static int MSG_INV_ERROR = 10;
    private static final String Y_M_D_T_H_M_S_000_Z = "%H:%M:%S";
    private boolean isReadding = false;
    private Time pTmpTime = new Time();
    private Handler mHandler = null;
    private DisplayThread mDisplayThread = null;// 刷新卡显示线程
    private InvThread mInvThread = null; // 读卡线程
    private boolean m_isBuzzer = true;

    private ReentrantLock mCardListLock = new ReentrantLock(); // 卡列表锁
    private Vector<Card> mCardList = new Vector<Card>();// 卡的集合

    public ReadCardCtrl(Handler pHandler) {
        mHandler = pHandler;
    }

    public void StartRead() {
        if (mDisplayThread == null && mInvThread == null) {
            isReadding = true;
            ApiCtrl.mIsReading = true;
            mDisplayThread = new DisplayThread();
            mDisplayThread.setName("DisplayThread");
            mDisplayThread.start();
            mInvThread = new InvThread();
            mInvThread.setName("InvThread");
            mInvThread.start();
        }
    }

    public void StopRead() {
        if (mInvThread != null && mDisplayThread != null) {
            isReadding = false;
            ApiCtrl.mIsReading = false;
            mInvThread.interrupt();
            mDisplayThread.interrupt();
            mDisplayThread = null;
            mInvThread = null;
        }

    }

    public void setBuzzer(boolean buzzer) {
        m_isBuzzer = buzzer;
    }

    public boolean IsReadding() {
        return isReadding;
    }

    public void GetCardList(ArrayList<HashMap<String, Object>> pCardList,
                            Context pContext) {
        mCardListLock.lock();
        HashMap<String, Object> pMap = null;
        Card pCard = null;
        int cardListSize = mCardList.size();
        for (int i = pCardList.size(); i < cardListSize; i++) {
            pMap = new HashMap<String, Object>();
            pCardList.add(pMap);
        }

        for (int i = 0; i < mCardList.size(); i++) {
            pCard = mCardList.get(i);
            pMap = pCardList.get(i);

            pMap.put("sn", "" + pCard.Index);
            pMap.put("readcount", "" + pCard.Record);
            pMap.put("cardid", pCard.EPCStr);
            pMap.put("state",
                    GetTagState(pCard.nTagType, pCard.nTagState, pContext));
            pMap.put("readtime", pCard.ReadTime);
            pMap.put(
                    "battery",
                    GetTagDescribe(pCard.nTagType, pCard.nTagBattery,
                            pCard.nRSSI, pContext));
            pMap.put("rssi", pCard.nRSSI);
        }

        mCardListLock.unlock();
    }

    private String GetTagDescribe(int nTagType, int nBattery, int nRSSI,
                                  Context pContext) {
        String retStr = "";
        retStr += GetNarmalTagDescribe(nBattery, nRSSI, pContext);
        return retStr;
    }


    /**
     * 普通标签描述
     *
     * @param nBattery
     * @param nRSSI
     * @param pContext
     * @return
     */
    private String GetNarmalTagDescribe(int nBattery, int nRSSI,
                                        Context pContext) {
        String retStr = "";
        int nBatteryBit = nBattery&0x01;
        if (nBatteryBit == 0) {
            retStr = "normal";
        } else {
            retStr = "low";
        }
        return retStr;
    }

    /**
     * @param nTagType  //卡类型
     * @param nTagState //卡状态
     * @param pContext
     * @return
     */
    private String GetTagState(int nTagType, int nTagState, Context pContext) {
        String retStr = "";
        retStr = pContext.getResources().getString(
                R.string.tag_type_normal);
        return retStr;
    }

    /**
     * 读到一张卡, 添加一张卡
     *
     * @param CardID
     * @return
     */

    public Card AddCard(INT32U CardID, INT8U nTagType, INT8U pBit,
                        INT32U nRSSI, INT32U nParam1) {
        Card retCard = null;

        String CardIDStr = "" + CardID.GetLongValue();

        mCardListLock.lock();

        for (Iterator<Card> iterator = mCardList.iterator(); iterator.hasNext(); ) {
            Card pCard = iterator.next();
            if (pCard.EPCStr.contentEquals(CardIDStr)) {
                retCard = pCard;

                break;
            }
        }

        if (retCard == null) {
            retCard = new Card();
            retCard.Index = mCardList.size() + 1;
            retCard.EPCStr = CardIDStr;
            retCard.Record = 0;
            mCardList.add(retCard);
        }

        pTmpTime.setToNow();

        retCard.Record++;
        retCard.ReadTime = pTmpTime.format(Y_M_D_T_H_M_S_000_Z);
        retCard.nTagState = pBit.GetValue();
        retCard.nTagType = nTagType.GetValue();
        retCard.nTagBattery = nParam1.GetValue();
        nRSSI.SetByte((byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) (nRSSI.GetByte4() + 1));
        retCard.nRSSI = nRSSI.GetValue();

        mCardListLock.unlock();

        if (m_isBuzzer)
            Util.play(1, 0, 1, 1);

        return retCard;
    }

    static int printBinaryInt(int i) {
        int ret = 0;
        System.out.println("int:" + i + ",binary:");
        System.out.print(" ");
        for (int j = 31; j >= 0; j--)
            if (((1 << j) & i) != 0)
                System.out.print("1");
            else
                System.out.print("0");
        System.out.println();

        return ret;
    }

    public void ClearAllCard() {
        synchronized (mCardList) {
            mCardListLock.lock();
            mCardList.clear();
            mCardListLock.unlock();
        }
    }

    public class Card {
        public int Index; // 序号
        public int Record; // 读卡次数
        public String EPCStr;// 卡号
        public int nTagType;// 卡类型
        public int nTagState;// 卡状态
        public int nTagBattery;// 卡电量
        public int nRSSI;// 场强
        public String ReadTime;// 读卡时间
    }

    // 显示线程
    private class DisplayThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                // 定时一秒刷新一次
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    interrupt();
                }
                Message msg = new Message();
                msg.what = MSG_INV_DISPLAY;
                mHandler.sendMessage(msg);
            }
            mDisplayThread = null;
        }
    }

    // 盘存线程
    private class InvThread extends Thread {
        @Override
        public void run() {
            super.run();

            int retN = 0;
            while (!isInterrupted()) {

                INT8U nTagType = new INT8U();
                INT32U pId = new INT32U();
                INT8U pBit = new INT8U();
                INT32U nRSSI = new INT32U();
                INT32U nParam1 = new INT32U();

                retN = ApiCtrl.SAATYRevIDMsgDecExpand(nTagType, pId,
                        nRSSI, nParam1);

                if (retN == 1) {
                    AddCard(pId, nTagType, pBit, nRSSI, nParam1);
                } else {
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        interrupt();
                    }
                }
            }
        }
    }
}
