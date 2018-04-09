package com.rt.video.decode;

import android.graphics.SurfaceTexture;

import com.gensee.media.IVideoIndication;
import com.gensee.media.VideoData;
import com.gensee.utils.GenseeLog;
import com.rt.video.decode.glcustom.CustomGlViewRender;
import com.rt.video.decode.glcustom.OffScreenGlRender;

import javax.microedition.khronos.egl.EGLContext;

public class Receiver264Data implements IVideoIndication, OffScreenGlRender.OnGlTextrueListener{
    private static final String TAG = "Receiver264Data";
    private Decode decode;
    private CustomGlViewRender glViewRender;
    private GlVideoRender glVideoRender;
    private OffScreenGlRender offScreenGlRender;

//    public Receiver264Data(Surface surface)
//    {
//        decode = new Decode(surface);
//    }
//
//    public Receiver264Data(int texId)
//    {
//        decode = new Decode(texId);
//    }

    public Receiver264Data()
    {
        glVideoRender = new GlVideoRender();
        glViewRender = new CustomGlViewRender();
        glViewRender.setGlRender(glVideoRender);

        offScreenGlRender = new OffScreenGlRender();
        offScreenGlRender.setOnGlTextrueListener(this);
        offScreenGlRender.onSurfaceCreated(null, 320, 240);
    }

    public void onSurfaceCreated(SurfaceTexture surfaceTexture, int mWidth, int mHeight)
    {
        glViewRender.onSurfaceCreated(surfaceTexture, mWidth, mHeight);
//        if(null != decode)
//        {
//            offScreenGlRender.getTextureRes();
//        }
    }

    public void onSurfaceChanged(int mWidth, int mHeight)
    {
        glViewRender.onSurfaceChanged(mWidth, mHeight);
    }

    public void onSurfaceDestroy()
    {
        glViewRender.onSurfaceDestroy();
    }

    @Override
    public void onReceiveFrame(byte[] bytes, int width, int height) {
        if(null != decode) {
            decode.putH264Data(bytes, width, height);
        }
    }

    @Override
    public void onReceiveFrame(VideoData data) {
        onReceiveFrame(data.getData(), data.getWidth(), data.getHeight());
    }

    @Override
    public void renderDefault() {

    }

    @Override
    public void onGlSurfaceTexture(SurfaceTexture surfaceTexture) {
        if(null != glViewRender)
        {
            glViewRender.setSurfaceTexture(surfaceTexture);
        }
        decode = new Decode(surfaceTexture);
    }

    @Override
    public void onGlContext(EGLContext eglContext, int mTextId) {
        if(null != glViewRender)
        {
            glViewRender.onGlContext(eglContext, mTextId);
        }
    }

    @Override
    public void onRequestRender() {
        if(null != glViewRender)
        {
            glViewRender.requestRender();
        }
    }
}
