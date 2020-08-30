package com.middleware.request;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RequestTcpServer extends IoHandlerAdapter
{
    public RequestTcpServer(int port) throws IOException {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setHandler(this);
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("TCP服务启动，端口：" + port);
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {

//        IoBuffer bbuf = (IoBuffer) message;
//        byte[] byten = new byte[bbuf.limit()];
//        bbuf.get(byten, bbuf.position(), bbuf.limit());
//        //System.out.println("收到消息：" + ByteAndStr16.Bytes2HexString(byten));
//        byte[] bts = new byte[10];
//        for(int i=0;i<10;i++){
//            bts[i] = (byte)i;
//        }


//        IoBuffer buffer = IoBuffer.allocate(10);
//        buffer.put(bts);
//        buffer.flip();
//        session.write(buffer);


//      // 拿到所有的客户端Session
//      Collection<IoSession> sessions = session.getService().getManagedSessions().values();
//      // 向所有客户端发送数据
//      for (IoSession sess : sessions) {
//          sess.write(buffer);
//      }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("会话关闭");
    }
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        System.out.println("会话异常");
        super.exceptionCaught(session, cause);
    }
    @Override
    public void messageSent(IoSession iosession, Object obj) throws Exception {
        System.out.println("服务端消息发送");
        super.messageSent(iosession, obj);
    }
    @Override
    public void sessionCreated(IoSession iosession) throws Exception {
        System.out.println("会话创建");
        super.sessionCreated(iosession);
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
    }
}
