package com.middleware.request;

import android.util.Log;

import com.middleware.frame.common.INT32U;
import com.middleware.frame.common.PrintCtrl;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.RFrameList;
import com.middleware.frame.data.Tools;
import com.middleware.frame.main.FrameMsgObervable;
import com.middleware.frame.main.MsgMngr;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;

public class RequestTcpClient extends IoHandlerAdapter implements Observer
{
    private IoConnector connector;
    private IoSession session;

    private DataProc mProc = new DataProc();
    private RFrameList mRFrameList = new RFrameList();
    private FrameMsgObervable toAndroid  = MsgMngr.PcToAndroidMsgObv;

    public RequestTcpClient(String ipAddr, int port) {
        try {
            connector = new NioSocketConnector();
            connector.setHandler(this);
            ConnectFuture connFuture = connector.connect(new InetSocketAddress(ipAddr, port));
            connFuture.awaitUninterruptibly();
            session = connFuture.getSession();
            System.out.println("TCP 客户端启动");
        }catch (Exception e){
            if (session.isConnected()){
                session.closeNow();
                session.getCloseFuture().awaitUninterruptibly();
            }
            connector.dispose();
            throw e;
        }

        MsgMngr.AndroidToPcTagObv.addObserver(this);
    }

    @Override
    public void messageReceived(IoSession iosession, Object message)
            throws Exception {
        IoBuffer bbuf = (IoBuffer) message;
        byte[] buffer = new byte[bbuf.limit()];
        int bsize = bbuf.limit();
        bbuf.get(buffer, bbuf.position(), bsize);

        PrintCtrl.PrintBUffer("数据从PC读取 ", buffer, bsize);

        if (bsize > 0)
        {

            mProc.UnPackMsg(buffer,bsize);
            mProc.GetFrameList(mRFrameList);
            for (int i = 0; i < mRFrameList.GetCount(); i++) {
                RFrame pRFrame = mRFrameList.GetRFrame(i);
                RFIDFrame rfidFrame = new RFIDFrame(pRFrame);
                toAndroid.dealFrame(rfidFrame);
                sendResultToPc(iosession,rfidFrame);
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
        buffer.put(pForSend,0,totalFrameSize.GetValue());
        buffer.flip();
        iosession.write(buffer);
        PrintCtrl.PrintBUffer("数据发送到PC ", pForSend, totalFrameSize.GetValue());
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
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        System.out.println("客户端异常");
        super.exceptionCaught(session, cause);
    }
    @Override
    public void messageSent(IoSession iosession, Object obj) throws Exception {
        System.out.println("客户端消息发送");
        super.messageSent(iosession, obj);
    }
    @Override
    public void sessionClosed(IoSession iosession) throws Exception {
        System.out.println("客户端会话关闭");
        session.closeNow();
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
    }


    @Override
    public void update(Observable o, Object arg) {
        //如果已经断开了, 就把数据丢了
       if (session == null)
       {
           return;
       }

       if (!session.isConnected())
       {
           return;
       }

        if ( MsgMngr.AndroidToPcTagObv == o)
        {
            RFrame frame = (RFrame) arg;
            assert  (frame != null);

            byte[] pForSend = new byte[DataProc.SEND_FRAME_MAXBUFF];
            INT32U totalFrameSize = new INT32U(DataProc.SEND_FRAME_MAXBUFF);

            mProc.PackMsg(pForSend,totalFrameSize, frame);

            IoBuffer buffer = IoBuffer.allocate(10);
            buffer.put(pForSend,0,totalFrameSize.GetValue());
            buffer.flip();
            session.write(buffer);

            PrintCtrl.PrintBUffer("标签数据发送到PC TCP-Client:", pForSend, totalFrameSize.GetValue());
        }
    }
}
