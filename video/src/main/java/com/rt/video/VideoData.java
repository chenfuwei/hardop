package com.rt.video;

public class VideoData {
    private byte[] data;
    private int width;
    private int height;
    private int frameType;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        if(this.data == null)
        {
            this.data = new byte[data.length];
        }
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFrameType() {
        return frameType;
    }

    public void setFrameType(int frameType) {
        this.frameType = frameType;
    }
}
