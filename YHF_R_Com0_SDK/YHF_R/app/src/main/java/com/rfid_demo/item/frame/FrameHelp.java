package com.rfid_demo.item.frame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.uart.R;
import com.rfid_demo.ctrl.ApiCtrl;

import java.lang.ref.WeakReference;

/**
 * 帮助
 *
 * @author sjq
 */
public class FrameHelp extends Fragment {

    protected WeakReference<View> mRootView;
    private TextView mDemoVerTv = null;
    private TextView mApiVerTv = null;
    private View rootView;//

    private String mDemoVerString = "2020061401 V1.0.2.1";

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
            rootView = inflater.inflate(R.layout.fragment_main_detail_help,
                    container, false);
            mRootView = new WeakReference<View>(rootView);

            mDemoVerTv = rootView.findViewById(R.id.textView3);
            mApiVerTv = rootView.findViewById(R.id.textView5);

            mDemoVerTv.setText(mDemoVerString);
            mApiVerTv.setText(ApiCtrl.SAATCopyright());
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