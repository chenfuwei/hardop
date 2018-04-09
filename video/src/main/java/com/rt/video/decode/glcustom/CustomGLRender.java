package com.rt.video.decode.glcustom;

import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;

import com.gensee.utils.GenseeLog;
import com.rt.video.decode.GlRender;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import static javax.microedition.khronos.egl.EGL10.EGL_ALPHA_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_BLUE_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_DEFAULT_DISPLAY;
import static javax.microedition.khronos.egl.EGL10.EGL_DEPTH_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_GREEN_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_RED_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE;

public class CustomGLRender implements Runnable{
    private static final String TAG = "CustomGLRender";
    protected GlRender glRender;
    EGL10 mEGL;
    EGLDisplay mEGLDisplay;
    EGLConfig[] mEGLConfigs;
    EGLConfig mEGLConfig;
    EGLContext mEGLContext;
    EGLSurface mEGLSurface;
    GL10 mGL;
    String mThreadOwner;
    Thread glThread;
    EGLContext mShareEGLContext;

    protected AtomicBoolean bRunning = new AtomicBoolean(false);

    protected Object object;
    protected AtomicBoolean bSurfaceChange = new AtomicBoolean(false);
    protected AtomicBoolean bSurfaceUpdate = new AtomicBoolean(false);

    protected boolean bEglSurfaceSuccess = false;

    public void onSurfaceCreated(SurfaceTexture surfaceTexture, int mWidth, int mHeight) {
        if(!bRunning.get()) {
            object = new Object();
            bRunning.set(true);
            glThread = new Thread(this, this.getClass().getSimpleName());
            glThread.start();
        }else
        {
            requestRender();
        }
    }

    public void onSurfaceChanged(int width, int height)
    {
        bSurfaceChange.set(true);
        requestRender();
    }

    public void onSurfaceDestroy()
    {
        GenseeLog.i(TAG, "onSurfaceDestroy");
        release();
    }

    public void setGlRender(GlRender glRender)
    {
        this.glRender = glRender;
    }

    private EGLConfig chooseConfig(){
        int[] attribList = new int[]{
                EGL_DEPTH_SIZE, 0,
                EGL_STENCIL_SIZE, 0,
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL_NONE
        };

        int[] numConfig = new int[1];
        mEGL.eglChooseConfig(mEGLDisplay, attribList, null, 0, numConfig);
        int configSize = numConfig[0];
        mEGLConfigs = new EGLConfig[configSize];
        mEGL.eglChooseConfig(mEGLDisplay, attribList, mEGLConfigs, configSize, numConfig);
        return mEGLConfigs[0];
    }

    private EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig)
    {
        int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        int[] attrs = {
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        GenseeLog.i(TAG, mShareEGLContext == null ? "YES" : "NO");
        EGLContext context = null == mShareEGLContext ? EGL10.EGL_NO_CONTEXT : mShareEGLContext;
        return egl.eglCreateContext(eglDisplay, eglConfig, context, attrs);
    }

    protected void initEglContext()
    {
        int[] version = new int[2];
        mEGL = (EGL10)EGLContext.getEGL();
        mEGLDisplay = mEGL.eglGetDisplay(EGL_DEFAULT_DISPLAY);
        mEGL.eglInitialize(mEGLDisplay, version);
        mEGLConfig = chooseConfig();
        mEGLContext = createContext(mEGL, mEGLDisplay, mEGLConfig);
    }

    protected void initEglSurface()
    {
        createSurface();
        mGL = (GL10)mEGLContext.getGL();

        //设置当前的渲染环境
        try {
            if (mEGLSurface == null || mEGLSurface == EGL10.EGL_NO_SURFACE) {
                throw new RuntimeException("GL error:" + GLUtils.getEGLErrorString(mEGL.eglGetError()));
            }
            if (!mEGL.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
                throw new RuntimeException("GL Make current Error"+ GLUtils.getEGLErrorString(mEGL.eglGetError()));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        // Record thread owner of OpenGL context
        mThreadOwner = Thread.currentThread().getName();

        bEglSurfaceSuccess = true;
    }

    public void requestRender()
    {
        synchronized (object) {
            object.notifyAll();
        }
    }

    synchronized public void release()
    {
        bRunning.set(false);
        if(null != glThread)
        {
            try{
                requestRender();
                glThread.interrupt();
//                glThread.join();
                glThread = null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void destroyEglSurface()
    {
        if(!mEGL.eglMakeCurrent(mEGLDisplay,EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT))
        {
            GenseeLog.w(TAG, "eglMakeCurrent failure");
        }

        if(!mEGL.eglDestroySurface(mEGLDisplay, mEGLSurface))
        {
            GenseeLog.w(TAG, "eglDestroySurface failure");
        }
        bEglSurfaceSuccess = false;
    }

    protected void destroy()
    {
        destroyEglSurface();
        if(!mEGL.eglDestroyContext(mEGLDisplay, mEGLContext))
        {
            GenseeLog.w(TAG, "eglDestroyContext failure");
        }

        if(!mEGL.eglTerminate(mEGLDisplay))
        {
            GenseeLog.w(TAG, "eglTerminate failure");
        }
    }

    protected void setRender(GlRender render)
    {

    }

//    public void updateSurfaceTexture(SurfaceTexture surfaceTexture, int mWidth, int mHeight)
//    {
//        this.mWidth = mWidth;
//        this.mHeight = mHeight;
//        this.surfaceTexture = surfaceTexture;
//        bSurfaceUpdate.set(true);
//        requestRender();
//        // bSurfaceChange.set(true);
//    }

    @Override
    public void run() {
        initEglContext();
        initEglSurface();
        setRender(glRender);

        while (bRunning.get())
        {
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

            synchronized (object)
            {
            onDrawFrame();
            mEGL.eglSwapBuffers(mEGLDisplay, mEGLSurface);


                try
                {
                    if(bRunning.get())
                    {
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

    protected void createSurface()
    {

    }

    protected void processSurfaceChange()
    {

    }

    protected void onDrawFrame()
    {

    }
}
