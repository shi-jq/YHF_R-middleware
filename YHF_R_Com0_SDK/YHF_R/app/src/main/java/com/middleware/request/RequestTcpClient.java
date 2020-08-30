package com.middleware.request;

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
    private static IoSession session;

    public RequestTcpClient(String ipAddr, int port) {
        connector = new NioSocketConnector();
        connector.setHandler(this);
        ConnectFuture connFuture = connector.connect(new InetSocketAddress(ipAddr, port));
        connFuture.awaitUninterruptibly();
        session = connFuture.getSession();
        System.out.println("TCP 客户端启动");
    }

    @Override
    public void messageReceived(IoSession iosession, Object message)
            throws Exception {
        IoBuffer bbuf = (IoBuffer) message;
        byte[] byten = new byte[bbuf.limit()];
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        //System.out.println("客户端收到消息" + ByteAndStr16.Bytes2HexString(byten));
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

    }
}
