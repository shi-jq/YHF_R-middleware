package com.middleware.frame.data;


public class RFrame implements Cloneable {
    public static final int HEAD_LEN = 5;
    public static final int COMMAND_LEN = 1;
    public static final int ANSWER_LEN = 1;
    public static final int CRC_LEN = 2;
    public static final int CMD_DATA_BUFFER = DataProc.SEND_FRAME_MAXBUFF - HEAD_LEN;
    public byte[] bHead = new byte[5];
    private byte[] bData = new byte[CMD_DATA_BUFFER];

    private int mRealHeadLen = 0;
    private int mRealDataLen = 0;

    public RFrame clone() throws CloneNotSupportedException {
        RFrame ret = (RFrame) super.clone();
        ret.bHead = this.bHead.clone();
        ret.bData = this.bData.clone();
        ret.mRealHeadLen = this.mRealHeadLen;
        ret.mRealDataLen = this.mRealDataLen;
        return ret;
    }


    public int GetRealBuffLen() {
        return this.mRealHeadLen + this.mRealDataLen;
    }


    public byte GetRfidCommand() {
        return this.bHead[4];
    }


    public byte[] GetData() {
        return this.bData;
    }


    public int GetLength() {
        return this.bHead[3];
    }

    public int GetDataLength() {
        return this.bHead[3] - COMMAND_LEN;
    }

    public int GetMustLength() {
        return this.bHead[3] + HEAD_LEN + CRC_LEN - COMMAND_LEN;
    }


    public int GetErroCode() {
        return this.bData[0];
    }


    public void Insert(byte data) {
        if (this.mRealDataLen == CMD_DATA_BUFFER) {
            return;
        }

        if (this.mRealHeadLen < 5) {
            this.bHead[this.mRealHeadLen] = data;
            this.mRealHeadLen++;
        } else {
            this.bData[this.mRealDataLen] = data;
            this.mRealDataLen++;
        }
    }

    public void Insert(byte[] data, int len) {
        for (int i = 0; i < len; i++) {
            Insert(data[i]);
        }
    }

    public byte GetBusAddr() {
        return this.bHead[2];
    }

    public void SetBusAddr(byte bus) {
        this.bHead[2] = bus;
    }


    public byte GetByte(int index) {
        if (index < 5) {
            return this.bHead[index];
        }
        return this.bData[index - 5];
    }

    public byte[] GetBytes(int start, int end) {
        end = end + 1;
        int count = end - start;
        if (count <= 0) {
            return new byte[0];
        }

        byte[] bytes = new byte[count];
        for (int i = 0; i < count && (i + start) < GetRealBuffLen(); i++) {
            bytes[i] = GetByte(start + i);
        }

        return bytes;
    }

    public void resetDataLen(int realDataLen)
    {
        this.mRealDataLen = realDataLen-HEAD_LEN+CRC_LEN;
    }

    public void resetHeadLenForReal()
    {
        this.bHead[3] = (byte) (this.mRealDataLen+COMMAND_LEN-CRC_LEN);
    }

    public void SetByte(int index, byte b) {
        if (index < 5) {
            this.bHead[index] = b;
        } else {
            this.bData[index - 5] = b;
        }
    }

    public void SetCrc(short crc) {
        if (this.mRealDataLen < 2) {
            return;
        }

        this.bData[this.mRealDataLen - 1] = (byte) (crc & 0xFF);
        this.bData[this.mRealDataLen - 2] = (byte) (byte) ((crc >>> 8) & 0xff);
    }


    public int GetCrc() {
        if (this.mRealDataLen < 2) {
            return 0;
        }

        return this.bData[this.mRealDataLen - 1] + (this.bData[this.mRealDataLen - 2] << 8);
    }


    public void InitData(byte[] data, int datalen) {
        System.arraycopy(data, 0, this.bData, 0, datalen);
        this.mRealDataLen = datalen;
    }

    public void InitHeadAndData(byte[] data, int datalen) {
        if (datalen < HEAD_LEN) {
            return;
        }

        System.arraycopy(data, 0, this.bHead, 0, HEAD_LEN);
        System.arraycopy(data, HEAD_LEN, this.bData, 0, datalen - HEAD_LEN);
        mRealHeadLen = 5;
        mRealDataLen = datalen-mRealHeadLen;
    }
}
