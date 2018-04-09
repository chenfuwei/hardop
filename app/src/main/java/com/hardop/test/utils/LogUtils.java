package com.hardop.test.utils;

import android.util.Log;

/**
 * Created by Administrator on 2018/3/30.
 */

public class LogUtils {
    private static final String TAG = "hardop";

    public static void logd(String msg)
    {
        logd("", msg);
    }

    public static void logd(String tag, String msg)
    {
        Log.d(TAG, tag +" " + msg);
    }

    public static void logi(String msg)
    {
        logi("", msg);
    }

    public static void logi(String tag, String msg)
    {
        Log.i(TAG, tag + " " + msg);
    }

    public static void logw(String msg)
    {
        logw("", msg);
    }

    public static void logw(String tag, String msg)
    {
        Log.w(TAG, tag + " " + msg);
    }

    public static void loge(String errorMsg)
    {
        loge("", errorMsg);
    }

    public static void loge(String tag, String errorMsg)
    {
        Log.e(TAG, tag +" " + errorMsg);
    }
}
