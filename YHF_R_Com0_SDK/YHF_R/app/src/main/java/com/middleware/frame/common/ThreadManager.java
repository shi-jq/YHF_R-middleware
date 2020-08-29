package com.middleware.frame.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadManager {
    public static final String DEFAULT_SINGLE_POOL_NAME = "DEFAULT_SINGLE_POOL_NAME";
    private static ThreadPoolProxy mDownloadPool = null;
    private static Object mDownloadLock = new Object();

    private static Map<String, ThreadPoolProxy> mMap = new HashMap<String, ThreadPoolProxy>();
    private static Object mSingleLock = new Object();


    public static ThreadPoolProxy getDownloadPool() {
        synchronized (mDownloadLock) {

            if (mDownloadPool == null) {
                mDownloadPool = new ThreadPoolProxy(5, 5, 5L);
            }
            return mDownloadPool;
        }
    }


    public static ThreadPoolProxy getSinglePool() {
        return getSinglePool("DEFAULT_SINGLE_POOL_NAME");
    }


    public static ThreadPoolProxy getSinglePool(String name) {
        synchronized (mSingleLock) {
            ThreadPoolProxy singlePool = mMap.get(name);
            if (singlePool == null) {
                singlePool = new ThreadPoolProxy(1, 1, 5L);
                mMap.put(name, singlePool);
            }
            return singlePool;
        }
    }

    public static class ThreadPoolProxy {
        private ThreadPoolExecutor mPool;
        private int mCorePoolSize;
        private int mMaximumPoolSize;
        private long mKeepAliveTime;

        private ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.mCorePoolSize = corePoolSize;
            this.mMaximumPoolSize = maximumPoolSize;
            this.mKeepAliveTime = keepAliveTime;
        }


        public synchronized void execute(Runnable run) {
            if (run == null) {
                return;
            }
            if (this.mPool == null || this.mPool.isShutdown()) {


                this.mPool = new ThreadPoolExecutor(this.mCorePoolSize,
                        this.mMaximumPoolSize, this.mKeepAliveTime,
                        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            }
            this.mPool.execute(run);
        }


        public synchronized void cancel(Runnable run) {
            if (this.mPool != null && (!this.mPool.isShutdown() || this.mPool.isTerminating())) {
                this.mPool.getQueue().remove(run);
            }
        }


        public synchronized boolean contains(Runnable run) {
            if (this.mPool != null && (!this.mPool.isShutdown() || this.mPool.isTerminating())) {
                return this.mPool.getQueue().contains(run);
            }
            return false;
        }


        public void stop() {
            if (this.mPool != null && (!this.mPool.isShutdown() || this.mPool.isTerminating())) {
                this.mPool.shutdownNow();
            }
        }


        public synchronized void shutdown() {
            if (this.mPool != null && (!this.mPool.isShutdown() || this.mPool.isTerminating()))
                this.mPool.shutdownNow();
        }
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\common\ThreadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */