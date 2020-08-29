package com.middleware.frame.common;


public class INT32U {
    private int value;

    public INT32U() {
        this.value = 0;
    }


    public INT32U(int value) {
        this.value = value & 0xFFFFFFFF;
    }

    public int GetValue() {
        return this.value;
    }

    public long GetLongValue() {
        long tmp = this.value;
        if (this.value < 0) {
            tmp <<= 32L;
            tmp >>>= 32L;
        }
        return tmp;
    }

    public void SetValue(int value) {
        this.value = value & 0xFFFFFFFF;
    }


    public byte GetByte1() {
        return (byte) ((0xFF000000 & this.value) >>> 24);
    }


    public byte GetByte2() {
        return (byte) ((0xFF0000 & this.value) >>> 16);
    }


    public byte GetByte3() {
        return (byte) ((0xFF00 & this.value) >>> 8);
    }


    public byte GetByte4() {
        return (byte) (0xFF & this.value);
    }


    public void SetByte(byte b1, byte b2, byte b3, byte b4) {
        this.value = ((0xFF & b1) << 24) + ((0xFF & b2) << 16) + ((
                0xFF & b3) << 8) + (0xFF & b4);
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\INT32U.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */