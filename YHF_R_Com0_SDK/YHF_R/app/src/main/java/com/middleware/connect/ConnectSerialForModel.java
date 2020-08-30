package com.middleware.connect;

public class ConnectSerialForModel extends ConnectSerial {

    public ConnectSerialForModel(SerialConfig config) {
        super(config);
    }

    public boolean init() {

        mSerialPort.rfid_poweron();
        mSerialPort.scanerpoweron();
        return super.init();
    }

    public boolean quit() {

        mSerialPort.setGPIOlow(89);
        mSerialPort.scaner_poweroff();
        mSerialPort.rfid_poweroff();
        return super.quit();
    }

    public boolean reconnect() {
        quit();
        init();
        return true;
    }

}
