package com.rfid_demo.item.frame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.uart.R;
import com.rfid_demo.ctrl.ApiCtrl;

import java.lang.ref.WeakReference;

/**
 * 退出程序
 *
 * @author sjq
 */
public class FrameQuit extends Fragment {

    protected WeakReference<View> mRootView;
    private View rootView;//

    private TextView mDemoVerTv = null;
    private TextView mApiVerTv = null;

    private String mDemoVerString = "2020061401 V1.0.2.1";

    private Button mQuitBtn = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null || mRootView.get() == null) {
            rootView = inflater.inflate(R.layout.fragment_main_detail_quit,
                    container, false);
            mRootView = new WeakReference<View>(rootView);
            mQuitBtn = rootView.findViewById(R.id.button1);

            mDemoVerTv = rootView.findViewById(R.id.textView3);
            mApiVerTv = rootView.findViewById(R.id.textView5);

            mDemoVerTv.setText(mDemoVerString);
            mApiVerTv.setText(ApiCtrl.SAATCopyright());

            mQuitBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    ApiCtrl.Quit();
                    getActivity().finish();
                }
            });
        } else {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
            rootView = mRootView.get();
        }

        return rootView;
    }

//	@Override
//	public boolean CanSkip() {
//
//		if (ApiCtrl.mIsReading) {
//			AppCfg.ShowMsg(AppCfg.getString(R.string.reading_card), false);
//			return false;
//		}
//		return true;
//	}
}