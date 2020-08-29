package com.rfid_demo.item.frame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.uart.R;
import com.middleware.frame.common.INT8U;
import com.rfid_demo.ctrl.ApiCtrl;
import com.rfid_demo.ctrl.AppCfg;

import java.lang.ref.WeakReference;
import java.util.Vector;

public class FrameSysInfo extends Fragment {

    protected WeakReference<View> mRootView;
    private View rootView;//
    private boolean mIsFirst = true;

    private Button mFreqPwrSetBtn = null;
    private Button mFreqPwrQueryBtn = null;
    private Button mCpQueryBtn = null;
    private Button mCpSetBtn = null;
    private EditText mAttenuationText = null;
    private Spinner mRecvPwrSpinner = null;
    private Spinner mSendPwrSpinner = null;
    private Spinner mCpSpinner = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null || mRootView.get() == null) {
            rootView = inflater.inflate(R.layout.fragment_main_detail_sysinfo,
                    container, false);
            mRootView = new WeakReference<View>(rootView);

            mFreqPwrSetBtn = rootView.findViewById(R.id.button1);
            mFreqPwrQueryBtn = rootView.findViewById(R.id.Button01);
            mCpSetBtn = rootView.findViewById(R.id.button2);
            mCpQueryBtn = rootView.findViewById(R.id.Button02);
            mAttenuationText = rootView.findViewById(R.id.editText);
            mCpSpinner = rootView.findViewById(R.id.spinner5);
            mRecvPwrSpinner = rootView.findViewById(R.id.Spinner01);
            mSendPwrSpinner = rootView.findViewById(R.id.spinner4);

            Vector<String> FreqPwrList = new Vector<String>();
            String str = "";
            str = "0dB";
            FreqPwrList.add(str);
            for (int i = 2; i < 32; i += 2) {
                str = "-" + i + "dB";
                FreqPwrList.add(str);
            }

            Vector<String> WaveParaList = new Vector<String>();
            for (int i = 2405; i < 2521; i += 5) {
                str = "" + i + "MHz";
                WaveParaList.add(str);
            }
            ArrayAdapter array = new ArrayAdapter<String>(rootView.getContext(),
                    android.R.layout.simple_spinner_item, WaveParaList);
            mCpSpinner.setAdapter(array);

            Vector<String> RecvPowerList = new Vector<String>();

            RecvPowerList.add(getString(R.string.high_gain));
            RecvPowerList.add(getString(R.string.lower_gain));

            array = new ArrayAdapter<String>(rootView.getContext(),
                    android.R.layout.simple_spinner_item, RecvPowerList);
            mRecvPwrSpinner.setAdapter(array);

            Vector<String> SendPowerList = new Vector<String>();
            str = "-18dB";
            SendPowerList.add(str);
            str = "-12dB";
            SendPowerList.add(str);
            str = "-6dB";
            SendPowerList.add(str);
            str = "0dB";
            SendPowerList.add(str);
            array = new ArrayAdapter<String>(rootView.getContext(),
                    android.R.layout.simple_spinner_item, SendPowerList);
            mSendPwrSpinner.setAdapter(array);

            mFreqPwrSetBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] btPara = new byte[12];
                    int iAttenuator = Integer.parseInt(mAttenuationText.getText().toString());
                    int iRecvPower = mRecvPwrSpinner.getSelectedItemPosition() + 2;
                    int iSendPower = mSendPwrSpinner.getSelectedItemPosition();
                    btPara[0] = 0x03;
                    btPara[1] = 0x03;
                    btPara[2] = 0x03;
                    btPara[3] = 0x00;
                    btPara[4] = (byte) iRecvPower;
                    btPara[5] = (byte) iSendPower;
                    btPara[6] = (byte) iAttenuator;
                    btPara[7] = 0x00;
                    btPara[8] = 0x00;
                    btPara[9] = 0x00;
                    btPara[10] = 0x00;
                    btPara[11] = 0x00;

                    boolean retB = ApiCtrl.SAATYAntennaParmSet(btPara, 12);
                    if (retB) {
                        AppCfg.ShowMsg(getString(R.string.set_succeed), false);
                    } else {
                        AppCfg.ShowMsg(getString(R.string.set_failed), true);
                    }
                }
            });

            mFreqPwrQueryBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    INT8U iAttenuator = new INT8U();
                    INT8U iRecvPower = new INT8U();
                    INT8U iSendPower = new INT8U();

                    boolean retB = ApiCtrl.SAATYAntennaParmQuery(iRecvPower,
                            iSendPower, iAttenuator);
                    if (retB) {
                        AppCfg.ShowMsg(getString(R.string.query_succeed), false);
                        mAttenuationText.setText(String.valueOf(iAttenuator.GetValue()));
                        mRecvPwrSpinner.setSelection(iRecvPower.GetValue() - 2);
                        mSendPwrSpinner.setSelection(iSendPower.GetValue());
                    } else {
                        AppCfg.ShowMsg(getString(R.string.query_failed), true);
                    }
                }
            });

            mCpSetBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = mCpSpinner.getSelectedItemPosition();
                    boolean retB = ApiCtrl.SAATYRFParaSet(0x00, index);
                    if (retB) {
                        AppCfg.ShowMsg(getString(R.string.set_succeed), false);
                    } else {
                        AppCfg.ShowMsg(getString(R.string.set_failed), true);
                    }
                }
            });
            mCpQueryBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    INT8U index = new INT8U();
                    boolean retB = ApiCtrl.SAATYRFParaQuery(0x00, index);
                    if (retB) {
                        AppCfg.ShowMsg(getString(R.string.query_succeed), false);
                        mCpSpinner.setSelection(index.GetValue());
                    } else {
                        AppCfg.ShowMsg(getString(R.string.query_failed), true);
                    }
                }
            });

        } else {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
            rootView = mRootView.get();
        }

        if (mIsFirst && ApiCtrl.mIsOpen) {
            mCpQueryBtn.performClick();
            mFreqPwrQueryBtn.performClick();
            mIsFirst = false;
        }

        return rootView;
    }

//	@Override
//	public boolean CanSkip() {
//		return super.CanSkip();
//	}

}