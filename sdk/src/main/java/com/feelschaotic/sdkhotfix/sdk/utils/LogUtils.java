package com.feelschaotic.sdkhotfix.sdk.utils;


import android.util.Log;

public class LogUtils {


    private static boolean debug = true;

    public static void init(boolean mode) {
        debug = mode;
    }

    public static void d(String tag, String msg) {
        if (debug) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (debug) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (debug) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            Log.e(tag, msg);
        }
    }

    public static void printError(Throwable e) {
        if (debug && e != null) {
            e.printStackTrace();
        }
    }
}
