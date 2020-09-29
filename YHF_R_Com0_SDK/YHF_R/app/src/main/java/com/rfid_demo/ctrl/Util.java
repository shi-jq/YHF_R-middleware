package com.rfid_demo.ctrl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Process;

import com.example.uart.R;
import com.middleware.frame.common.INT8U;
import com.rfid_demo.main.MainActivity;
import com.rfid_demo.main.MyApplication;

import java.util.HashMap;
import java.util.Map;

public class Util {
    private static final String DATA_FILE_NAME = "YHF_R";
    public static SoundPool sp ;
    public static Map<Integer, Integer> suondMap;
    public static Context context;
    public static Context dtContext;


    //
    public static void initSoundPool(Context context){
        Util.context = context;
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        suondMap = new HashMap<Integer, Integer>();
        suondMap.put(1, sp.load(context, R.raw.beep2, 1));
    }

    //
    public static  void play(int sound, int number,float audioCurrentVolume,float volumnRatio){
        AudioManager am = (AudioManager)Util.context.getSystemService(Context.AUDIO_SERVICE);
        //
        float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (audioCurrentVolume == 0)
        {
            audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

        int Volume = (int) (volumnRatio*audioMaxVolume);
        //Log.i("test volume", "play: ---------------"+Volume);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,Volume, AudioManager.FLAG_PLAY_SOUND);

        sp.play(
                suondMap.get(sound), //
                audioCurrentVolume, //
                audioCurrentVolume, //
                1, //
                number, //
                1);//

    }
    public static void pasue() {
        sp.pause(0);
    }

    public static void dtSave(String key, Object obj) {
        SharedPreferences sp = dtContext.getSharedPreferences(DATA_FILE_NAME, dtContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof Boolean) {
            editor.putBoolean(key, (Boolean) obj);
        } else if (obj instanceof Float) {
            editor.putFloat(key, (Float) obj);
        } else if (obj instanceof Integer) {
            editor.putInt(key, (Integer) obj);
        } else if (obj instanceof Long) {
            editor.putLong(key, (Long) obj);
        } else {
            editor.putString(key, (String) obj);
        }
        editor.commit();
    }

    public static Object dtGet( String key, Object defaultObj) {
        SharedPreferences sp = dtContext.getSharedPreferences(DATA_FILE_NAME, dtContext.MODE_PRIVATE);
        if (defaultObj instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObj);
        } else if (defaultObj instanceof Float) {
            return sp.getFloat(key, (Float) defaultObj);
        } else if (defaultObj instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObj);
        } else if (defaultObj instanceof Long) {
            return sp.getLong(key, (Long) defaultObj);
        } else if (defaultObj instanceof String) {
            return sp.getString(key, (String) defaultObj);
        }
        return defaultObj;
    }

    public static byte tenShow16StrByte (int num){
            String  secondStr = String.format("%02d",num);
            Integer s = Integer.parseInt(secondStr,16);
            return new INT8U(s).GetValue() ;
        }

    public static Object dtGet( String key) {
      return dtGet(key,null);
    }

    //通过崩溃重启app
    public static void  restartApp()
    {

        Intent intent = new Intent();
        Context context = MyApplication.contxt;
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC,System.currentTimeMillis() + 1000,pendingIntent);

        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
