package com.rfid_demo.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uart.R;
import com.middleware.frame.ctrl.RfidErro;
import com.rfid_demo.ctrl.ApiCtrl;
import com.rfid_demo.ctrl.AppCfg;
import com.rfid_demo.ctrl.Util;
import com.rfid_demo.item.frame.FrameQuit;
import com.rfid_demo.item.frame.FrameReadCard;
import com.rfid_demo.item.frame.FrameSysInfo;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private MyApplication mapp;

    private FragmentManager mFm; //fragment manager
    private FragmentTransaction mFt;//fragment transaction
   // private FrameOpen fragment1;
    private FrameReadCard fragment2;
    private FrameSysInfo fragment3;
   // private FrameHelp fragment4;
    private FrameQuit fragment5;


    private TextView textView_title;

   // private TextView textView_f1;
    private TextView textView_f2;
    private TextView textView_f3;
    //private TextView textView_f4;
    private TextView textView_f5;
    private Fragment mFragmentCurrent;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView(); // Init UI
        mapp = (MyApplication) getApplication();

        AppCfg.mMain = this;

        Util.initSoundPool(this);//Init sound pool
    }

    private void initView() {
        //fragment1 = new FrameOpen();
        fragment2 = new FrameReadCard();
        fragment3 = new FrameSysInfo();
        //fragment4 = new FrameHelp();
        fragment5 = new FrameQuit();


        mFragmentCurrent = fragment2;
        mFm = getSupportFragmentManager();
        mFt = mFm.beginTransaction();
        mFt.add(R.id.framelayout_main, fragment2);
        mFt.commit();

        textView_title = findViewById(R.id.title);

        //textView_f1 = findViewById(R.id.textView_f1);
        textView_f2 = findViewById(R.id.textView_f2);
        textView_f3 = findViewById(R.id.textView_f3);
        //textView_f4 = findViewById(R.id.textView_f4);
        textView_f5 = findViewById(R.id.textView_f5);
       // textView_f1.setClickable(true);
        textView_f2.setClickable(true);
        textView_f3.setClickable(true);
        //textView_f4.setClickable(true);
        textView_f5.setClickable(true);
       // textView_f1.setOnClickListener(this);
        textView_f2.setOnClickListener(this);
        textView_f3.setOnClickListener(this);
        //textView_f4.setOnClickListener(this);
        textView_f5.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.textView_f1:
//                if (ApiCtrl.mIsReading) {
//                    showToast(getString(R.string.info_stopRead));
//                    return;
//                }
//                switchContent(fragment1);
//                textView_f1.setTextColor(getResources().getColor(R.color.blu));
//                textView_title.setText(R.string.start_mod);
//                break;
            case R.id.textView_f2:
                switchContent(fragment2);
                textView_f2.setTextColor(getResources().getColor(R.color.blu));
                textView_title.setText(R.string.read_card);
                break;
            case R.id.textView_f3:
                if (!ApiCtrl.mIsOpen) {
                    showToast(getString(R.string.info_openModule));
                    return;
                }
                if (ApiCtrl.mIsReading) {
                    showToast(getString(R.string.info_stopRead));
                    return;
                }
                switchContent(fragment3);
                textView_f3.setTextColor(getResources().getColor(R.color.blu));
                textView_title.setText(R.string.sys_info);
                break;
//            case R.id.textView_f4:
//                switchContent(fragment4);
//                textView_f4.setTextColor(getResources().getColor(R.color.blu));
//                textView_title.setText(R.string.app_vesion);
//                break;
            case R.id.textView_f5:
                if (ApiCtrl.mIsOpen) {
                    showToast(getString(R.string.info_stopModule));
                    return;
                }
                if (ApiCtrl.mIsReading) {
                    showToast(getString(R.string.info_stopRead));
                    return;
                }
                switchContent(fragment5);
                textView_f5.setTextColor(getResources().getColor(R.color.blu));
                textView_title.setText(R.string.quit_app);
                break;
        }
    }

    //switch fragments
    public void switchContent(Fragment to) {
//        Log.e("switch",""+to.getId());
//        textView_f1.setTextColor(getResources().getColor(R.color.white));
        textView_f2.setTextColor(getResources().getColor(R.color.white));
        textView_f3.setTextColor(getResources().getColor(R.color.white));
//        textView_f4.setTextColor(getResources().getColor(R.color.white));
        textView_f5.setTextColor(getResources().getColor(R.color.white));
        if (mFragmentCurrent != to) {
            mFt = mFm.beginTransaction();
            if (!to.isAdded()) {    //
                mFt.hide(mFragmentCurrent).add(R.id.framelayout_main, to).commit(); //
            } else {
                mFt.hide(mFragmentCurrent).show(to).commit(); //
            }
            mFragmentCurrent = to;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ApiCtrl.Quit();
    }

    public void ShowMsg(String msg, boolean isErro) {

        if (isErro) {
            RfidErro pRfidErro = ApiCtrl.GetErroCode();
            if (pRfidErro != null) {
                msg += ":";
                msg += pRfidErro.erroCode.toString();
            }
        }
        showToast(msg);
    }

    //show toast
    private void showToast(String info) {
        if (mToast == null)
            mToast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        else
            mToast.setText(info);
        mToast.show();
    }
}
