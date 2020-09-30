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

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;

/**
 * tcp服务端,用来接收客户端的连接, 不需要支持多用户, 只要单用户即可 ,如果一个连接, 踢掉其他的即可
 *
 */
public class RequestTcpServer extends IoHandlerAdapter  implements Observer
{

    private DataProc mProc = new DataProc();
    private RFrameList mRFrameList = new RFrameList();
    private FrameMsgObervable toAndroid  = MsgMngr.PcToAndroidMsgObv;
    private static IoSession readTagSession = null;
    private IoSession session;

    public RequestTcpServer(int port) throws IOException {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setHandler(this);
        acceptor.setReuseAddress(true);
        acceptor.bind(new InetSocketAddress(port));
        acceptor.getSessionConfig().setKeepAlive(true);

        Log.v("TCPSERVER", "TCP服务启动，端口：" + port);

        MsgMngr.AndroidToPcTagObv.addObserver(this);
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {
        this.session = session;
        IoBuffer bbuf = (IoBuffer) message;
        byte[] buffer = new byte[bbuf.limit()];
        int bsize = bbuf.limit();
        bbuf.get(buffer, bbuf.position(), bsize);

        PrintCtrl.PrintBUffer("数据从PC读取  -TCP-Server ", buffer, bsize);

        if (bsize > 0)
        {
            mProc.UnPackMsg(buffer,bsize);
            mProc.GetFrameList(mRFrameList);
            for (int i = 0; i < mRFrameList.GetCount(); i++) {
                RFrame pRFrame = mRFrameList.GetRFrame(i);
                mProc.mBusAddr = pRFrame.GetBusAddr();
                RequestModel reqModel = new RequestModel(pRFrame, mProc, RequestModel.ReqType.SERVER);
                if (ConfigMngr.canHandlerReqModel(reqModel) == RequestModel.FailHandler)
                {
                    RFIDFrame rfidFrame = new RFIDFrame(pRFrame);
                    toAndroid.dealFrame(rfidFrame);
                    if (mProc.isStartRead( pRFrame.GetRfidCommand()))
                    {
                        this.readTagSession = session;
                    }
                    sendResultToPc(session, rfidFrame);
                }
            }
            mRFrameList.ClearAll();
        }
        this.session = null;
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
        PrintCtrl.PrintBUffer("数据发送到PC   -TCP-Server ", pForSend, totalFrameSize.GetValue());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("会话关闭");
        if (this.readTagSession == session )
        {
            this.readTagSession = null;
        }

    }
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        System.out.println("会话异常");
        cause.printStackTrace();
        super.exceptionCaught(session, cause);
        if (this.readTagSession == session )
        {
            this.readTagSession = null;
        }
    }
    @Override
    public void messageSent(IoSession iosession, Object obj) throws Exception {
        System.out.println("服务端消息发送");
        super.messageSent(iosession, obj);
        System.out.println("服务端消息发送:"+obj.toString());
    }
    @Override
    public void sessionCreated(IoSession iosession) throws Exception {
        System.out.println("会话创建");
        super.sessionCreated(iosession);

        this.readTagSession = iosession;
    }
    @Override
    public void sessionIdle(IoSession iosession, IdleStatus idlestatus)
            throws Exception {
        System.out.println("会话休眠");
        super.sessionIdle(iosession, idlestatus);
    }
    @Override
    public void sessionOpened(IoSession iosession) throws Exception {
        System.out.println("会话打开");
        super.sessionOpened(iosession);

        this.readTagSession = iosession;
    }

    @Override
    public void update(Observable o, Object arg) {
        if ( MsgMngr.AndroidToPcTagObv == o)
        {
            RFrame frame = (RFrame) arg;
            assert  (frame != null);

            /*
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

            if (readTagSession != null)
            {
                IoBuffer buffer = IoBuffer.allocate(10);
                buffer.setAutoExpand(true);
                buffer.put(pForSend,0,totalFrameSize.GetValue());
                buffer.flip();
                readTagSession.write(buffer);
                PrintCtrl.PrintBUffer("标签数据发送到PC -TCP-Server ", pForSend, totalFrameSize.GetValue());
            }
        }
    }
}
