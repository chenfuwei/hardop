package com.rt.video.decode.glcustom;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.gensee.utils.GenseeLog;
import com.rt.video.decode.GlRender;

import javax.microedition.khronos.egl.EGLContext;

public class CustomGlViewRender extends CustomGLRender{

    private static final String TAG = "CustomGlViewRender";
    private SurfaceTexture surfaceTexture;
    int mWidth, mHeight;

    private int mTexId;

    boolean bSurfaceCreate = false;

    private SurfaceTexture decodeSurfaceTexture;

    public void setSurfaceTexture(SurfaceTexture surfaceTexture)
    {
        GenseeLog.i(TAG, "setSurfaceTexture surfaceTexture = " + surfaceTexture);
        decodeSurfaceTexture = surfaceTexture;
    }

    synchronized public void onSurfaceCreated(SurfaceTexture surfaceTexture, int mWidth, int mHeight)
    {
        GenseeLog.i(TAG, "onSurfaceCreated mWidth = " + mWidth + " mHeight = " + mHeight);
        this.surfaceTexture = surfaceTexture;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        bSurfaceCreate = true;
        if(null != mShareEGLContext) {
            GenseeLog.i(TAG, "onSurfaceCreated 1");
            super.onSurfaceCreated(surfaceTexture, mWidth, mHeight);
            bSurfaceUpdate.set(true);
        }else
        {
            GenseeLog.i(TAG, "onSurfaceCreated mShareContext is null");
        }
    }

    public void onSurfaceChanged(int width, int height)
    {
        GenseeLog.i(TAG, "onSurfaceChanged width = " + width + " height = " + height);
        if(this.mWidth != width || this.mHeight != height)
        {
            this.mWidth = width;
            this.mHeight = height;
            super.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onSurfaceDestroy() {
        //super.onSurfaceDestroy();
        bSurfaceCreate = false;
       // mShareEGLContext = null;
        //mTexId = 0;
    }

    protected void setRender(GlRender render)
    {
        if(!Thread.currentThread().getName().equals(mThreadOwner))
        {
            GenseeLog.e(TAG, "setGlRener: this thread does not own the Opengles Context");
            return;
        }


        glRender.onSurfaceCreated(mGL, mEGLConfig);
        glRender.onSurfaceChanged(mGL, mWidth, mHeight);
    }

    @Override
    protected void createSurface() {
        mEGLSurface = mEGL.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, surfaceTexture, null);
    }

    @Override
    protected void processSurfaceChange() {
        glRender.onSurfaceChanged(mGL, mWidth, mHeight);
    }

    private void _onDrawFrame() {

        GenseeLog.i(TAG, "onDrawFrame 1");

        if (null != decodeSurfaceTexture) {
            try {
                GenseeLog.i(TAG, "onDrawFrame 2");
                decodeSurfaceTexture.updateTexImage();
            } catch (Exception e) {
                e.printStackTrace();
                GenseeLog.i(TAG, "onDrawFrame 3");
            }
        }
        GenseeLog.i(TAG, "onDrawFrame 4");
        if(bEglSurfaceSuccess) {
            GenseeLog.i(TAG, "onDrawFrame 4-1");
            glRender.onDrawFrame(mGL, mTexId);
        }
        GenseeLog.i(TAG, "onDrawFrame 5");
    }

    @Override
    public void run() {
        initEglContext();
//        initEglSurface();
//        setRender(glRender);
        while (bRunning.get())
        {
            synchronized (object)
        {
            if(bSurfaceCreate && !bEglSurfaceSuccess)
            {
                initEglSurface();
                setRender(glRender);
            }

            if(!bSurfaceCreate)
            {
                destroyEglSurface();
            }

            if(bSurfaceUpdate.get())
            {
                bSurfaceUpdate.set(false);
                destroyEglSurface();
                initEglSurface();
                processSurfaceChange();
            }

            if(bSurfaceChange.get())
            {
                bSurfaceChange.set(false);
                processSurfaceChange();
            }


                _onDrawFrame();
                if(bEglSurfaceSuccess) {
                    mEGL.eglSwapBuffers(mEGLDisplay, mEGLSurface);
                }


                try
                {
                    if(bRunning.get())
                    {
                        GenseeLog.i("run 4");
                        object.wait();
                    }
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        destroy();
    }
    synchronized public void onGlContext(EGLContext eglContext, int mTexId) {
        GenseeLog.i(TAG, "onGlContext1 = " + eglContext + " mTexId = " + mTexId);
        if(null == mShareEGLContext) {
            GenseeLog.i(TAG, "onGlContext2 = " + eglContext + " mTexId = " + mTexId);
            this.mTexId = mTexId;
            mShareEGLContext = eglContext;
//            decodeSurfaceTexture = new SurfaceTexture(mTexId);
//            decodeSurfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);

            if (bSurfaceCreate) {
                GenseeLog.i(TAG, "onGlContext3 = " + eglContext + " mTexId = " + mTexId);
                super.onSurfaceCreated(surfaceTexture, mWidth, mHeight);
            }
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return decodeSurfaceTexture;
    }

    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            GenseeLog.i(TAG, "onFrameAvailable 111");
             requestRender();
        }
    };
}
