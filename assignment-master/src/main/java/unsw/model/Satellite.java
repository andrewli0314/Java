package unsw.model;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class Satellite extends Entity {


    private char buffer[];
    private int maxBufferLength;
    private int maxFileNum;
    private int sendBandWidth;
    private int receiveBandWidth;

    private int activeSendFileNum;
    private int activeReceiveFileNum;


    public Satellite (String satelliteId, double height, Angle position, int speed, int range,
                      int maxBufferLength, int maxFileNum, int sendBandWidth, int receiveBandWidth) {
        super(satelliteId, height, position, speed, range);
        this.maxBufferLength = maxBufferLength;
        this.maxFileNum = maxFileNum;
        this.sendBandWidth = sendBandWidth;
        this.receiveBandWidth = receiveBandWidth;
        this.buffer = new char[this.maxBufferLength];
        this.activeReceiveFileNum = 0;
        this.activeSendFileNum = 0;
    }

    public char[] getBuffer() {
        return buffer;
    }

    public void setBuffer(char[] buffer) {
        this.buffer = buffer;
    }

    public int getMaxBufferLength() {
        return maxBufferLength;
    }

    public void setMaxBufferLength(int maxBufferLength) {
        this.maxBufferLength = maxBufferLength;
    }

    public int getMaxFileNum() {
        return maxFileNum;
    }

    public void setMaxFileNum(int maxFileNum) {
        this.maxFileNum = maxFileNum;
    }

    public int getSendBandWidth() {
        return sendBandWidth;
    }

    public void setSendBandWidth(int sendBandWidth) {
        this.sendBandWidth = sendBandWidth;
    }

    public int getReceiveBandWidth() {
        return receiveBandWidth;
    }


    public void setReceiveBandWidth(int receiveBandWidth) {
        this.receiveBandWidth = receiveBandWidth;
    }
}
