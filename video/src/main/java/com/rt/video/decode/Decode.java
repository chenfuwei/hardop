package com.rt.video.decode;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import com.gensee.utils.GenseeLog;
import com.rt.video.VideoData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Decode {
    private static final String TAG = "Decode";
    private static final String TYPE = "video/avc";
    private MediaCodec mediaCodec;
    private List<VideoData> datas = new ArrayList<VideoData>();
    private SurfaceTexture mSurfaceTexture;
    private int mWidth = 320;
    private int mHeight = 240;
    private Surface mSurface;

    public Decode(Surface surface)
    {
        mSurface = surface;
    }

    public Decode(SurfaceTexture surfaceTexture)
    {
        mSurfaceTexture = surfaceTexture;
    }

    private SurfaceTexture.OnFrameAvailableListener  onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {

        }
    };

    public void putH264Data(byte[] data, int mWidth, int mHeight)
    {
        if(null == mediaCodec)
        {
            initDecode(mWidth,mHeight);
        }
        decode(data);
    }

    private void initDecode(int mWidth, int mHeight)
    {
        try {
            Surface surface = null;
            if(mSurface != null)
            {
                surface = mSurface;
            }else if(null != mSurfaceTexture)
            {
                surface = new Surface(mSurfaceTexture);
            }else
            {
                GenseeLog.e(TAG, "ERROR! No Surface");
                return;
            }
            mediaCodec = MediaCodec.createDecoderByType(TYPE);
            if(mWidth > 0 && mHeight > 0)
            {
                this.mWidth = mWidth;
                this.mHeight = mHeight;
            }

            MediaFormat videoFormat = MediaFormat.createVideoFormat(TYPE, this.mWidth, this.mHeight);
            mediaCodec.configure(videoFormat,surface, null,0);
            mediaCodec.start();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void decode(byte[] data)
    {
       int inputIndex =  mediaCodec.dequeueInputBuffer(-1);
       if(inputIndex >= 0) {
           ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputIndex);
           inputBuffer.clear();
           inputBuffer.put(data);
           mediaCodec.queueInputBuffer(inputIndex, 0, data.length, computePresentationTime(frameIndex), 0);
           frameIndex ++ ;
       }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
       int outputIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
       while (outputIndex >= 0) {
//           GenseeLog.i(TAG, "outputIndex = " + outputIndex);
           mediaCodec.releaseOutputBuffer(outputIndex, true);
           outputIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
       }
    }

    private int frameIndex = 0;
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / 15;
    }

}
