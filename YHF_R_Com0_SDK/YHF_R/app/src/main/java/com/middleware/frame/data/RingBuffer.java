package com.middleware.frame.data;


public class RingBuffer {
    private static final int RING_SIZE = 8192;
    private int m_used = 0;
    private int m_head = 0;
    private int m_tail = 0;
    private byte[] m_ring = new byte[8192];

    public boolean Empty() {
        return (this.m_used == 0);
    }

    public boolean Full() {
        return (this.m_used == 8192);
    }

    public int BytesUsed() {
        return this.m_used;
    }

    public int BytesFree() {
        return 8192 - this.m_used;
    }

    public void Clear() {
        this.m_head = this.m_tail = this.m_used = 0;
    }

    public int Add(byte[] pBuffer, int bufferSize) {
        assert bufferSize <= BytesFree();


        if (this.m_tail + bufferSize > 8192) {

            int appendSize = 8192 - this.m_tail;
            for (int i = 0; i < appendSize; i++) {
                this.m_ring[i + this.m_tail] = pBuffer[i];
            }


            int prependSize = bufferSize - appendSize;
            for (int i = 0; i < prependSize; i++) {
                this.m_ring[i] = pBuffer[i + appendSize];
            }
        } else {

            for (int i = 0; i < bufferSize; i++) {
                this.m_ring[this.m_tail + i] = pBuffer[i];
            }
        }


        this.m_used += bufferSize;
        this.m_tail = (this.m_tail + bufferSize) % 8192;

        return BytesFree();
    }

    public int Remove(byte[] pBuffer, int bufferSize) {
        assert bufferSize <= BytesUsed();


        if (this.m_head + bufferSize > 8192) {

            int firstCopy = 8192 - this.m_head;

            for (int i = 0; i < firstCopy; i++) {
                pBuffer[i] = this.m_ring[i + this.m_head];
            }


            int secondCopy = bufferSize - firstCopy;
            for (int i = 0; i < secondCopy; i++) {
                pBuffer[i + firstCopy] = this.m_ring[i];
            }
        } else {

            for (int i = 0; i < bufferSize; i++) {
                pBuffer[i] = this.m_ring[i + this.m_head];
            }
        }


        this.m_used -= bufferSize;
        this.m_head = (this.m_head + bufferSize) % 8192;

        return BytesUsed();
    }

    public byte Get(int index) {
        int realIndex = 0;
        if (this.m_head + index > 8192) {
            realIndex = index - 8192 - this.m_head;
        } else {
            realIndex = index + this.m_head;
        }
        return this.m_ring[realIndex];
    }

    public int find(int from, byte data) {
        int index = -1;
        for (int i = from; i < this.m_used; i++) {
            if (Get(i) == data) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int skip(int skipIndex) {
        assert skipIndex <= BytesUsed();


        this.m_used -= skipIndex;
        this.m_head = (this.m_head + skipIndex) % 8192;

        return BytesUsed();
    }
}


/* Location:              C:\Users\shi_j\Desktop\yrfid_api.jar!\com\yrfidapi\reader\data\RingBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.2
 */