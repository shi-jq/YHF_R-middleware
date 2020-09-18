package com.middleware.frame.common;


public class INT8U {
    private byte value;

    public INT8U() {
        this.value = 0;
    }

    INT8U(byte value) {
        this.value = (byte) (value & 0xFF);
    }


    public INT8U(int value) {
        this.value = (byte) (value & 0xFF);
    }

    public byte GetValue() {
        return this.value;
    }

    public void SetByte(byte value) {
        this.value = (byte) (value & 0xFF);
    }


    public void SetInt(int value) {
        this.value = (byte) (value & 0xFF);
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\INT8U.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */