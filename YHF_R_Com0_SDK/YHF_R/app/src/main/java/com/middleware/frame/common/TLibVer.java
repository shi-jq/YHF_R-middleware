package com.middleware.frame.common;


public enum TLibVer {
    TRANSLIB_MAJOR_VSN(2), TRANSLIB_MINOR_VSN(6), TRANSLIB_MAINTENANCE_VSN(0), TRANSLIB_RELEASE_VSN(
            240);

    private final int value;


    TLibVer(int value) {
        this.value = value;
    }

    public int GetValue() {
        return this.value;
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\TLibVer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */