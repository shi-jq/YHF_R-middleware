//package android_serialport_api;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//
//import com.taiim.bodecoder.R;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android_serialport_api.SerialPort;
//
//public class DemoActivity extends Activity implements OnClickListener{
//
//	private Button btn ;
//	private EditText et;
//
//	int baudrate = 9600;
//	String path = new String("/dev/ttyUSB0");
//	private SerialPort mSerialPort;
//	private String TAG = "DemoActivity";
//	private OutputStream mOutputStream;
//	private InputStream mInputStream;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.serialport_demo);
//
//		btn = (Button) findViewById(R.id.btn);
//		et = (EditText) findViewById(R.id.et);
//
//		btn.setOnClickListener(this);
//
//		init();
//
//	}
//
//	private void init() {
//		//尝试打开设备
//		try {
//			mSerialPort = new SerialPort(new File(path), baudrate, 0);
//		}
//		catch (SecurityException e) {
//			Log.i(TAG , "SecurityException");
//
//		}
//		catch (IOException e) {
//			Log.i(TAG, "IOException");
//		}
//		//板卡识别操作
//		mOutputStream = mSerialPort.getOutputStream();
//		mInputStream = mSerialPort.getInputStream();
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		String src1 = et.getText().toString();
//
//
//		try {
//			byte [] tests = src1.getBytes("gbk");
//			Log.v("", ">>>>>>>>>>>> src1 : " + tests);
//			try {
//				mOutputStream.write(tests);
//				mOutputStream.write("\n".getBytes());
//				//mOutputStream.flush();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//
//	}
//
//
//}
