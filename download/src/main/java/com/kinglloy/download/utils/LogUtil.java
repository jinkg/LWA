package com.kinglloy.download.utils;

import android.os.Environment;
import android.os.Process;
import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Locale;

/**
 * YaLin 2016/11/23.
 */

public class LogUtil {

    private static final String FILE_NAME = "LWA/download_log.txt";

    public static final boolean LOG_ENABLE = false;
    public static final boolean EXTERNAL_LOG_ENABLE = false;

    public static synchronized boolean isExternalLogEnabled() {
        //noinspection SimplifiableIfStatement
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return false;
        }
        return LOG_ENABLE && EXTERNAL_LOG_ENABLE;
    }

    public static void F(String tag, String msg, Throwable throwable) {
        if (!LOG_ENABLE) {
            return;
        }
        String stackTraces = Log.getStackTraceString(throwable);
        if (msg == null) {
            msg = "";
        }
        F(tag, msg + " :\n " + stackTraces);
    }

    public static void F(String tag, String msg) {
        if (!LOG_ENABLE) {
            return;
        }
        String procInfo = getProcessInfo();

        Log.e(tag, procInfo + msg);

        if (!isExternalLogEnabled()) {
            return;
        }
        tag = tag + ":debug";
        writeLog(tag, procInfo + msg);
    }

    public static void E(String tag, Throwable throwable) {
        E(tag, null, throwable);
    }

    public static void E(String tag, String msg, Throwable throwable) {
        if (!LOG_ENABLE) {
            return;
        }
        String stackTraces = Log.getStackTraceString(throwable);
        if (msg == null) {
            msg = "";
        }
        E(tag, msg + " :\n " + stackTraces);
    }

    public static void E(String tag, String msg) {
        if (!LOG_ENABLE) {
            return;
        }
        String procInfo = getProcessInfo();

        Log.e(tag, procInfo + msg);
    }

    public static void D(String tag, Throwable throwable) {
        D(tag, null, throwable);
    }

    public static void D(String tag, String msg, Throwable throwable) {
        if (!LOG_ENABLE) {
            return;
        }
        String stackTraces = Log.getStackTraceString(throwable);
        if (msg == null) {
            msg = "";
        }

        D(tag, msg + " :\n " + stackTraces);
    }

    public static void D(String tag, String msg) {
        if (!LOG_ENABLE) {
            return;
        }
        String procInfo = getProcessInfo();

        Log.d(tag, procInfo + msg);
    }

    private static String getProcessInfo() {
        return "Process id: " + Process.myPid()
                + " Thread id: " + Thread.currentThread().getId() + " ";
    }

    private static synchronized void writeLog(String tag, String msg) {
        internalWriteLog(FILE_NAME, tag, msg);
    }

    private static synchronized void internalWriteLog(String filename, String tag, String msg) {
        try {
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return;
            }

            File file = new File(Environment.getExternalStorageDirectory(), filename);

            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();

            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file, true)));

            String time = getCurrentTime();
            bw.write(time + " " + tag + " \t" + msg + "\r\n");

            bw.close();
        } catch (Exception e) {
            // ignore
        }
    }

    private static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        return String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:%02d.%03d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND),
                c.get(Calendar.MILLISECOND));
    }
}
