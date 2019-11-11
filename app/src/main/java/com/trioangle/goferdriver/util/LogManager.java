package com.trioangle.goferdriver.util;

import android.util.Log;

import com.trioangle.goferdriver.BuildConfig;

/**
 * Created by trioangle on 9/7/18.
 */

public class LogManager {

    private static final String TAG = "MCL";

    /**
     * Log Level Error
     **/
    public static void e(String message) {
        if (BuildConfig.DEBUG) CommonMethods.DebuggableLogE(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Warning
     **/
    public static void w(String message) {
        if (BuildConfig.DEBUG) Log.w(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Information
     **/
    public static void i(String message) {
        if (BuildConfig.DEBUG) CommonMethods.DebuggableLogI(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Debug
     **/
    public static void d(String message) {
        if (BuildConfig.DEBUG) CommonMethods.DebuggableLogD(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Verbose
     **/
    public static void v(String message) {
        if (BuildConfig.DEBUG) CommonMethods.DebuggableLogV(TAG, buildLogMsg(message));
    }

    private static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);

        return sb.toString();

    }

}
