package com.middleware.frame.common;


public class RfidErrorException
        extends Exception {
    private RfidStatus m_error;
    private String m_thrownBy;

    public RfidErrorException(RfidStatus error, String thrownBy) {
        this.m_error = error;
        this.m_thrownBy = thrownBy;
    }


    public RfidErrorException(RfidStatus error) {
        this.m_error = error;
        this.m_thrownBy = "";
    }

    public RfidStatus GetError() {
        return this.m_error;
    }

    public String GetThrownBy() {
        return this.m_thrownBy;
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\RfidErrorException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */