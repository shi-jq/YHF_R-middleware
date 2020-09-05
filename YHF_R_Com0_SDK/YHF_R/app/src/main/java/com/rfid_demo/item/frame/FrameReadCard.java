package com.rfid_demo.item.frame;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.uart.R;
import com.middleware.main.MiddlewareService;
import com.rfid_demo.ctrl.ApiCtrl;
import com.rfid_demo.ctrl.AppCfg;
import com.rfid_demo.ctrl.ReadCardCtrl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 读卡界面
 *
 * @author sjq
 */
public class FrameReadCard extends Fragment {

    protected WeakReference<View> mRootView;
    ReadCardCtrl mReadCardCtrl = null;
    ArrayList<HashMap<String, Object>> mDataList = null;
    private TextView CountCardTv = null; // 总数显示的textview
    private Button StartReadBtn = null;// 开始读卡按钮
    private Button EndReadBtn = null;// 结束读卡
    private Button ClearBtn = null;// 清空读卡记录
    private ListView m_listView = null;// 读卡列表
    private SimpleAdapter listAdapter = null;
    private CheckBox mBuzzerCkb = null;
    private Button mStartBtn = null;
    private Button mStopBtn = null;

    private MiddlewareService mServer = null;
    /*
     * 消息响应操作
     */
    @SuppressLint("HandlerLeak")
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ReadCardCtrl.MSG_INV_DISPLAY:
                    if (mReadCardCtrl != null) {
                        if (mReadCardCtrl.IsReadding()) {
                            UptadeTable();
                        }
                    }

                    break;

                case ReadCardCtrl.MSG_INV_ERROR:
                    // Log.v(TAG, "Message for MSG_SET_FINISH.");
                    // stopInv();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    };
    private View rootView;//
    private boolean f1hidden = false;
    //key receiver
    private long startTime = 0;
    private boolean keyUpFalg = true;
    private BroadcastReceiver keyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (f1hidden) return;
            int keyCode = intent.getIntExtra("keyCode", 0);
            if (keyCode == 0) {//H941
                keyCode = intent.getIntExtra("keycode", 0);
            }
//            Log.e("key ","keyCode = " + keyCode) ;
            boolean keyDown = intent.getBooleanExtra("keydown", false);
//			Log.e("key ", "down = " + keyDown);
            if (keyUpFalg && keyDown && System.currentTimeMillis() - startTime > 500) {
                keyUpFalg = false;
                startTime = System.currentTimeMillis();
                if ((keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F2
                        || keyCode == KeyEvent.KEYCODE_F3 || keyCode == KeyEvent.KEYCODE_F4 ||
                        keyCode == KeyEvent.KEYCODE_F5)) {
//                Log.e("key ","inventory.... " ) ;
                    runInventory();
                }
                return;
            } else if (keyDown) {
                startTime = System.currentTimeMillis();
            } else {
                keyUpFalg = true;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView != null && mRootView.get() != null) {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
            rootView = mRootView.get();
        } else {
            rootView = inflater.inflate(R.layout.fragment_main_detail_read,
                    container, false);
            mRootView = new WeakReference<View>(rootView);
            CountCardTv = rootView.findViewById(R.id.textView2);
            StartReadBtn = rootView.findViewById(R.id.button1);
            EndReadBtn = rootView.findViewById(R.id.button2);
            ClearBtn = rootView.findViewById(R.id.button3);
            mReadCardCtrl = new ReadCardCtrl(mHandler);
            mStartBtn = rootView.findViewById(R.id.button5);
            mStopBtn = rootView.findViewById(R.id.button6);
            mBuzzerCkb =  rootView.findViewById(R.id.checkBox);
            mBuzzerCkb.setChecked(true);

            m_listView = rootView.findViewById(R.id.listView);

            mDataList = new ArrayList<HashMap<String, Object>>();

            listAdapter = new SimpleAdapter(rootView.getContext(), mDataList,
                    R.layout.chlid_widget_read_listitem, new String[]{"sn",
                    "readcount", "cardid", "readtime",
                    "rssi","battery"}, new int[]{R.id.item_sn,
                    R.id.item_readcount, R.id.item_cardid, R.id.item_readtime,R.id.item_rssi,R.id.item_battery});
            // 实现列表的显示
            m_listView.setAdapter(listAdapter);
            ClearView();

            /**
             * 开始读卡
             */
            StartReadBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!ApiCtrl.mIsOpen) {
                        AppCfg.ShowMsg(R.string.info_openModule,true);
                        return;
                    }

                    boolean retB = false;
                    if (mReadCardCtrl != null) {
                        ClearView();
                        retB = OnMakeTagUpLoad();
                        if (retB) {
                            mReadCardCtrl.StartRead();
                            AppCfg.ShowMsg(R.string.readcard_success, false);
                            StartReadBtn.setEnabled(false);
                            EndReadBtn.setEnabled(true);
                        } else {
                            AppCfg.ShowMsg(R.string.readcard_failed, true);
                        }
                    }
                }
            });

            /**
             * 停止读卡
             */
            EndReadBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!ApiCtrl.mIsOpen) {
                        AppCfg.ShowMsg(R.string.info_openModule,true);
                        return;
                    }

                    ApiCtrl.SAATPowerOff();
                    mReadCardCtrl.StopRead();
                    AppCfg.ShowMsg(R.string.stop_readcard, false);
                    StartReadBtn.setEnabled(true);
                    EndReadBtn.setEnabled(false);
                }
            });

            /**
             * 清空界面记录
             */
            ClearBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClearView();
                }
            });

            mStartBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (mServer == null) {
                            mServer = new MiddlewareService();
                        }
                    } catch (Exception e) {

                    }

                    if (ApiCtrl.Initialize() && ApiCtrl.Open()) {
                        AppCfg.ShowMsg(R.string.open_success, false);
                        mStartBtn.setEnabled(false);
                        mStopBtn.setEnabled(true);
                    } else {
                        AppCfg.ShowMsg(R.string.open_failed, true);
                    }
                }
            });

            mStopBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mServer != null)
                    {
                        mServer = null;
                    }

                    if (ApiCtrl.mIsReading) {
                        AppCfg.ShowMsg(R.string.info_stopRead,true);
                        return;
                    }

                    if (ApiCtrl.Quit()) {
                        AppCfg.ShowMsg(R.string.close_success, false);
                        mStartBtn.setEnabled(true);
                        mStopBtn.setEnabled(false);
                    } else {
                        AppCfg.ShowMsg(R.string.close_failed, true);
                    }
                }
            });

            mBuzzerCkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mReadCardCtrl.setBuzzer(isChecked);
                }
            });
        }


        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        getActivity().registerReceiver(keyReceiver, filter);

        return rootView;
    }

    public void UptadeTable() {
        // 获取卡列表
        mReadCardCtrl.GetCardList(mDataList, getActivity()
                .getApplicationContext());
        listAdapter.notifyDataSetChanged();

        CountCardTv.setText("" + mDataList.size());
    }

    public void ClearView() {

        mReadCardCtrl.ClearAllCard();
        CountCardTv.setText("0");
        mDataList.clear();
        listAdapter.notifyDataSetChanged();
    }

    /**
     * 读取主动标签
     *
     * @return
     */
    public boolean OnMakeTagUpLoad() {
        int nOpType = 0x01; // 单次或循环
        int nIDType = 0x01;// ID类型，1为BID
        return ApiCtrl.SAATYMakeTagUpLoadIDCode(nOpType, nIDType);
    }

    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        f1hidden = hidden;
        if (hidden) {
            if (ApiCtrl.mIsReading)
                runInventory();// stop inventory
        }
    }

    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        if (ApiCtrl.mIsReading)
            runInventory();// stop inventory
    }

    private void runInventory() {
        if (ApiCtrl.mIsReading) {
            EndReadBtn.performClick();
        } else {
            StartReadBtn.performClick();
        }
    }

}
