package com.rfid_demo.item.frame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.uart.R;
import com.rfid_demo.ctrl.ApiCtrl;
import com.rfid_demo.ctrl.AppCfg;

import java.lang.ref.WeakReference;

/**
 * 开始
 *
 * @author sjq
 */
public class FrameOpen extends Fragment {
    protected WeakReference<View> mRootView;
    Button mStartBtn = null;
    Button mStopBtn = null;
    private View rootView;//

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

            rootView = inflater.inflate(R.layout.fragment_main_detail_start,
                    container, false);
            mRootView = new WeakReference<View>(rootView);

            mStartBtn = rootView.findViewById(R.id.button1);
            mStopBtn = rootView.findViewById(R.id.button2);

            mStartBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

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
                    if (ApiCtrl.Quit()) {
                        AppCfg.ShowMsg(R.string.close_success, false);
                        mStartBtn.setEnabled(true);
                        mStopBtn.setEnabled(false);
                    } else {
                        AppCfg.ShowMsg(R.string.close_failed, true);
                    }
                }
            });
        }

        return rootView;
    }

//	@Override
//	public boolean CanSkip() {
//		if (ApiCtrl.mIsReading) {
//			AppCfg.ShowMsg(R.string.reading_card, false);
//			return false;
//		}
//		return true;
//	}

}
