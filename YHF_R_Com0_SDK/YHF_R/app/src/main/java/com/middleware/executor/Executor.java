package com.middleware.executor;

import com.middleware.connect.ConnectBase;
import com.middleware.frame.common.BaseThread;
import com.middleware.frame.common.PrintCtrl;
import com.middleware.frame.ctrl.ReaderState;
import com.middleware.frame.data.DataProc;
import com.middleware.frame.data.RFIDFrame;
import com.middleware.frame.data.RFrame;
import com.middleware.frame.data.RFrameList;
import com.middleware.frame.main.FrameMsgObervable;
import com.middleware.frame.main.MsgMngr;
import com.middleware.reader.Reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

public class Executor  extends BaseThread implements Observer {

    private ReaderState readerStatus = ReaderState.READER_STATE_INIT;

    private DataProc mProc = new DataProc();
    private RFrameList mRecvRFrameList = new RFrameList();
    private ConnectBase connect = null;

    FrameMsgObervable toPc = null;
    FrameMsgObervable toModel = null;
    Executor()
    {
        super("Executor",true);
        MsgMngr.PcToAndroidObv.addObserver(this);
        MsgMngr.ModelToAndroidObv.addObserver(this);

        toPc = MsgMngr.AndroidToPcObv;
        toModel = MsgMngr.AndroidToModelObv;

    }
    @Override
    public void update(Observable o, Object arg)
    {
        if (o ==  MsgMngr.PcToAndroidObv)
        {
            //处理pc 到android的命令
            RFIDFrame rfidFrame = (RFIDFrame) arg;
            assert  (rfidFrame != null);

            toModel.sendFrame(rfidFrame);
        }
        else  if (o ==  MsgMngr.ModelToAndroidObv)
        {
            //处理modle 到pc的命令
            RFIDFrame rfidFrame = (RFIDFrame) arg;
            assert  (rfidFrame != null);


            toPc.sendFrame(rfidFrame);
        }
    }

    public boolean threadProcess() {
        return false;
    }


}
