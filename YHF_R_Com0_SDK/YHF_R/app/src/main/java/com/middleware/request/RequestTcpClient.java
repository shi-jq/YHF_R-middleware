package com.middleware.request;

import android.util.Log;

import com.middleware.config.ConfigMngr;
import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.PrintCtrl;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.RFrameList;
import com.middleware.frame.main.FrameMsgObervable;
import com.middleware.frame.main.MsgMngr;
import com.rfid_demo.ctrl.Util;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

public class RequestTcpClient extends IoHandlerAdapter implements Observer
{
    private IoConnector connector;

    private IoSession session = null;

    private DataProc mProc = new DataProc();
    private RFrameList mRFrameList = new RFrameList();
    private FrameMsgObervable toAndroid  = MsgMngr.PcToAndroidMsgObv;
    private String ipAddr;
    private int port;

    public RequestTcpClient(String ipAddr, int port)  {
        this.ipAddr = ipAddr;
        this.port = port;

        connector = new NioSocketConnector();
        connector.setHandler(this);
        connector.setConnectTimeoutMillis(3000);
        connector.setDefaultRemoteAddress(new InetSocketAddress(ipAddr, port));
        MsgMngr.AndroidToPcTagObv.addObserver(this);
    }

    public boolean connect() {
        try {
            ConnectFuture connFuture = connector.connect();
            connFuture.awaitUninterruptibly();
            synchronized(RequestTcpClient.class) {
                session = connFuture.getSession();
                if (session == null) {
                    return false;
                }
            }

        }catch (Exception e){
            synchronized(RequestTcpClient.class) {
                if (session != null && session.isConnected()) {
                    session.closeNow();
                }
                session = null;
            }
            return false;
        }

        System.out.println("TCP 客户端启动");
        return  true;
    }

    public  boolean isConnect()
    {
        synchronized(RequestTcpClient.class)
        {
            if (session != null )
            {
                return session.isConnected();
            }
        }
        return false;
    }

    @Override
    public void messageReceived(IoSession iosession, Object message)
            throws Exception {
        IoBuffer bbuf = (IoBuffer) message;
        byte[] buffer = new byte[bbuf.limit()];
        int bsize = bbuf.limit();
        bbuf.get(buffer, bbuf.position(), bsize);

        PrintCtrl.PrintBUffer("数据从PC读取  -TCP-Client  ", buffer, bsize);

        if (bsize > 0)
        {
            mProc.UnPackMsg(buffer,bsize);
            mProc.GetFrameList(mRFrameList);
            for (int i = 0; i < mRFrameList.GetCount(); i++) {
                RFrame pRFrame = mRFrameList.GetRFrame(i);
                mProc.mBusAddr = pRFrame.GetBusAddr();
                RequestModel reqModel = new RequestModel(pRFrame, mProc, RequestModel.ReqType.CLIENT);
                if (ConfigMngr.canHandlerReqModel(reqModel) == RequestModel.FailHandler)
                {
                    RFIDFrame rfidFrame = new RFIDFrame(pRFrame);
                    toAndroid.dealFrame(rfidFrame);
                    sendResultToPc(iosession,rfidFrame);
                }
            }
            mRFrameList.ClearAll();
        }
    }


    private void sendResultToPc(IoSession iosession,RFIDFrame rfidFrame)
    {
        assert  (rfidFrame != null);

        RFrame recvFrame = rfidFrame.GetRevFrame();
        if (recvFrame == null)
        {
            //有可能没有应答, 没有应答则不做处理,因为本身已经超时了
            return;
        }

        byte[] pForSend = new byte[DataProc.SEND_FRAME_MAXBUFF];
        INT32U totalFrameSize = new INT32U(DataProc.SEND_FRAME_MAXBUFF);

        mProc.PackMsg(pForSend,totalFrameSize, recvFrame);

        IoBuffer buffer = IoBuffer.allocate(10);
        buffer.setAutoExpand(true);
        buffer.put(pForSend,0,totalFrameSize.GetValue());
        buffer.flip();
        iosession.write(buffer);
        PrintCtrl.PrintBUffer("数据发送到PC  -TCP-Client  ", pForSend, totalFrameSize.GetValue());
    }

    public void sendToPC(RFIDFrame rfidFrame)
    {
        if (this.session == null || this.session.isClosing())
        {
            Log.i("sendResultToPc","Iosession invaild");
            return;
        }

        sendResultToPc(this.session,rfidFrame);
    }

    @Override
    public void exceptionCaught(IoSession iosession, Throwable cause)
            throws Exception {
        System.out.println("客户端异常");
        super.exceptionCaught(iosession, cause);
    }
    @Override
    public void messageSent(IoSession iosession, Object obj) throws Exception {
        //System.out.println("客户端消息发送");
        super.messageSent(iosession, obj);
    }
    @Override
    public void sessionClosed(IoSession iosession) throws Exception {
        System.out.println("客户端会话关闭");
        synchronized(RequestTcpClient.class) {
            if (session == iosession) {
                session.closeNow();
                session = null;
            }
        }
        super.sessionClosed(iosession);
    }
    @Override
    public void sessionCreated(IoSession iosession) throws Exception {
        System.out.println("客户端会话创建");
        super.sessionCreated(iosession);
    }
    @Override
    public void sessionIdle(IoSession iosession, IdleStatus idlestatus)
            throws Exception {
        System.out.println("客户端会话休眠");
        super.sessionIdle(iosession, idlestatus);
    }
    @Override
    public void sessionOpened(IoSession iosession) throws Exception {
        System.out.println("客户端会话打开");
        super.sessionOpened(iosession);
        synchronized(RequestTcpClient.class)
        {
           session = iosession;
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        //如果已经断开了, 就把数据丢了
        if ( MsgMngr.AndroidToPcTagObv == o)
        {
            RFrame dataframe = (RFrame) arg;
            RFrame frame = null;
            try {
                frame = (RFrame) dataframe.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return;
            }
            assert  (frame != null);
            //*
            int realLen = frame.GetRealBuffLen();
            Calendar now = Calendar.getInstance();
            byte second = Util.tenShow16StrByte(now.get(Calendar.SECOND));
            byte minute = Util.tenShow16StrByte(now.get(Calendar.MINUTE));
            byte hour = Util.tenShow16StrByte(now.get(Calendar.HOUR_OF_DAY));
            byte dayOfWeek = Util.tenShow16StrByte(now.get(Calendar.DAY_OF_WEEK));
            byte day = Util.tenShow16StrByte(now.get(Calendar.DAY_OF_MONTH));
            byte month = Util.tenShow16StrByte(now.get(Calendar.MONTH) + 1);
            byte year = Util.tenShow16StrByte(now.get(Calendar.YEAR)%100);
            realLen -= RFrame.CRC_LEN;
            frame.SetByte(realLen,second);
            realLen++;
            frame.SetByte(realLen,minute);
            realLen++;
            frame.SetByte(realLen,hour);
            realLen++;
            frame.SetByte(realLen,dayOfWeek);
            realLen++;
            frame.SetByte(realLen,day);
            realLen++;
            frame.SetByte(realLen,month);
            realLen++;
            frame.SetByte(realLen,year);
            realLen++;
            frame.resetDataLen(realLen-RFrame.CRC_LEN);
            frame.bHead[3] = (byte) (realLen-RFrame.HEAD_LEN+RFrame.COMMAND_LEN) ;
            mProc.updateFrameCrc(frame);
            //*/

            byte[] pForSend = new byte[DataProc.SEND_FRAME_MAXBUFF];
            INT32U totalFrameSize = new INT32U(DataProc.SEND_FRAME_MAXBUFF);

            mProc.PackMsg(pForSend,totalFrameSize, frame);

            IoBuffer buffer = IoBuffer.allocate(10);
            buffer.setAutoExpand(true);
            buffer.put(pForSend,0,totalFrameSize.GetValue());
            buffer.flip();
            PrintCtrl.PrintBUffer("标签数据发送到PC -TCP-Client -begin", pForSend, totalFrameSize.GetValue());
            synchronized(RequestTcpClient.class) {
                if (session == null) {
                    return;
                }
                if (!session.isConnected()) {
                    session.closeNow();
                    session = null;
                    return;
                }
                session.write(buffer);
                PrintCtrl.PrintBUffer("标签数据发送到PC TCP-Client:", pForSend, totalFrameSize.GetValue());
            }
        }
    }
}
