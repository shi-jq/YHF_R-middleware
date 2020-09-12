package com.rfid_demo.ctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.uart.R;

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
        return null;
    }


    public static Object dtGet( String key) {
      return dtGet(key,null);
    }

}
