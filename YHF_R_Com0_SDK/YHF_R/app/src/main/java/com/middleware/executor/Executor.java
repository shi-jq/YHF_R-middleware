package com.middleware.executor;

import com.middleware.frame.common.BaseThread;
import com.middleware.frame.ctrl.ReaderState;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.RFrameList;
import com.middleware.frame.main.FrameMsgObervable;
import com.middleware.frame.main.FrameTagObervable;
import com.middleware.frame.main.MsgMngr;

import java.util.Observable;
import java.util.Observer;

public class Executor  extends BaseThread implements Observer {

    private ReaderState readerStatus = ReaderState.READER_STATE_INIT;

    private DataProc mProc = new DataProc();
    private RFrameList mRecvRFrameList = new RFrameList();

    FrameMsgObervable toPc = null;
    FrameMsgObervable toModel = null;
    FrameTagObervable toPcTag = null;
    public Executor()
    {
        super("Executor",true);
        MsgMngr.PcToAndroidMsgObv.addObserver(this);

        MsgMngr.ModelToAndroidTagObv.addObserver(this);

        toModel = MsgMngr.AndroidToModelMsgObv;
        toPcTag = MsgMngr.AndroidToPcTagObv;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (o ==  MsgMngr.PcToAndroidMsgObv)
        {
            //处理pc 到android的命令
            RFIDFrame rfidFrame = (RFIDFrame) arg;
            assert  (rfidFrame != null);
            toModel.dealFrame(rfidFrame);
        }
        else  if (o ==  MsgMngr.ModelToAndroidTagObv)
        {
            RFrame rfidFrame = (RFrame) arg;
            assert  (rfidFrame != null);
            toPcTag.sendTag(rfidFrame);
        }
    }

    public boolean threadProcess() {
        return false;
    }

}
