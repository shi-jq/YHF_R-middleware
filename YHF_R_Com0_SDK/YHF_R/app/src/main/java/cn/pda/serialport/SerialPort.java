package cn.pda.serialport;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SerialPort
{
    private static final String TAG = "SerialPort";

    static  {
       // System.loadLibrary("devapi");
        System.loadLibrary("serial_port");
    }


    private FileDescriptor mFd;


    private FileInputStream mFileInputStream;

    private FileOutputStream mFileOutputStream;


    public SerialPort(File file, int baudrate, int flags) throws SecurityException, IOException {
        this.mFd = open(file.getAbsolutePath(), baudrate, flags);
        Log.e("SerialPort", "open SerialPort port=" + file.getAbsolutePath() + "baudrate=" + baudrate);
        if (this.mFd == null) {
            Log.e("SerialPort", "native open returns null");
            throw new IOException();
        }


        this.mFileInputStream = new FileInputStream(this.mFd);
        this.mFileOutputStream = new FileOutputStream(this.mFd);
    }


    public InputStream getInputStream() { return this.mFileInputStream; }



    public OutputStream getOutputStream() { return this.mFileOutputStream; }


    private static native FileDescriptor open(String paramString, int paramInt1, int paramInt2);

    public native void close();

}
