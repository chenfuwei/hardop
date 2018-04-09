package com.rt.video.decode.glcustom;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.gensee.utils.GenseeLog;
import com.rt.video.decode.GlRender;

import javax.microedition.khronos.egl.EGLContext;

import static javax.microedition.khronos.egl.EGL10.EGL_HEIGHT;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_WIDTH;

public class OffScreenGlRender extends CustomGLRender{
    private static final String TAG = "OffScreenGlRender";
    private int mWidth;
    private int mHeight;
    private int mTexId;

    private SurfaceTexture decodeSurfaceTexture = null;
    private OnGlTextrueListener onGlTextrueListener;

    public void setOnGlTextrueListener(OnGlTextrueListener listener)
    {
        onGlTextrueListener = listener;
    }

    public void onSurfaceCreated(SurfaceTexture surfaceTexture, int mWidth, int mHeight)
    {
        GenseeLog.i(TAG, "onSurfaceCreated mWidth = " + mWidth + " mHeight = " + mHeight);
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        super.onSurfaceCreated(surfaceTexture, mWidth, mHeight);
    }

    @Override
    protected void createSurface() {
        int[] attribList = new int[] {
                EGL_WIDTH, mWidth,
                EGL_HEIGHT, mHeight,
                EGL_NONE
        };
        mEGLSurface = mEGL.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, attribList);
    }

    protected void setRender(GlRender render)
    {
        if(!Thread.currentThread().getName().equals(mThreadOwner))
        {
            GenseeLog.e(TAG, "setGlRener: this thread does not own the Opengles Context");
            return;
        }

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mTexId = textures[0];

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTexId);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        decodeSurfaceTexture = new SurfaceTexture(mTexId);
        decodeSurfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
        if (null != onGlTextrueListener) {
            onGlTextrueListener.onGlSurfaceTexture(decodeSurfaceTexture);
        }

        GenseeLog.i(TAG, "mTexId = " + mTexId);
        if(null != onGlTextrueListener)
        {
            onGlTextrueListener.onGlContext(mEGLContext, mTexId);
        }
        if(null != glRender) {
            glRender.onSurfaceCreated(mGL, mEGLConfig);
            glRender.onSurfaceChanged(mGL, mWidth, mHeight);
        }
    }

    public void getTextureRes()
    {
        if(null != onGlTextrueListener)
        {
            onGlTextrueListener.onGlContext(mEGLContext, mTexId);
        }
    }

    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            GenseeLog.i(TAG, "onFrameAvailable threadid = " + Thread.currentThread().getName() + ":" + Thread.currentThread().getId());
           // requestRender();
            if(null != onGlTextrueListener)
            {
                onGlTextrueListener.onRequestRender();
            }
        }
    };

    @Override
    protected void onDrawFrame() {
//        if(null != decodeSurfaceTexture)
//        {
//            decodeSurfaceTexture.updateTexImage();
//        }
    }

    public interface OnGlTextrueListener{
        void onGlSurfaceTexture(SurfaceTexture surfaceTexture);
        void onGlContext(EGLContext eglContext, int mTextId);
        void onRequestRender();
    }
}
