package com.middleware.frame.common;

public class INT16U {
    private int value;

    public INT16U(int value) {
        this.value = value & 0xFFFF;
    }

    public int GetValue() {
        return this.value;
    }

    public void SetValue(int value) {
        this.value = value & 0xFFFF;
    }


    public byte GetByte1() {
        return (byte) ((0xFF00 & this.value) >> 8);
    }


    public byte GetByte2() {
        return (byte) (0xFF & this.value);
    }


    public void SetValue(byte b1, byte b2) {
        this.value = ((b1 & 0xFF) << 8) + (b2 & 0xFF);
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\INT16U.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */