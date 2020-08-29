package com.middleware.frame.common;


public class RftserW32 {
    public static long Ser32_TickCount() {
        return System.currentTimeMillis();
    }


    public static boolean Ser_CheckTimeout(long start, int hasElapsed) {
        long deadline = start + hasElapsed;
        boolean rc = false;

        long now = Ser32_TickCount();

        if (deadline < start) {


            if (now >= deadline && now < start)
                rc = true;
        } else if (now >= deadline) {
            rc = true;
        } else if (now < start) {
            rc = true;
        }
        return rc;
    }

    public static int Ser_MsToSeconds(long ms) {
        if (ms == 0L) {
            return 0;
        }

        return (int) (ms / 1000L);
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\RftserW32.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */