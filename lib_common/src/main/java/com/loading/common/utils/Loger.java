package com.loading.common.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Loger {
    private final static String LOGTAG = "HelloWorld";
    private static boolean isDebug = true;
    private static final int LOG_FILE_LINE_CNT = 200;
    private static final int LOG_FILE_MAX_CNT = 5;


    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static void v(String tag, String log) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void v(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void d(String tag, String log) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void d(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void i(String tag, String log) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void i(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.i(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void w(String tag, String log) {
        if (isDebug) {
            android.util.Log.w(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void w(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.w(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void e(String tag, String log) {
        if (isDebug) {
            android.util.Log.e(LOGTAG, getLogMsg(tag, log));
        }
    }

    public static void e(String tag, String log, Throwable e) {
        if (isDebug) {
            android.util.Log.e(LOGTAG, getLogMsg(tag, log), e);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (isDebug) {
            android.util.Log.e(LOGTAG, getLogMsg(null, msg), tr);
        }
    }

    public static void wtf(String tag, String log) {
        android.util.Log.wtf(LOGTAG, getLogMsg(tag, log));
    }

    public static void wtf(String tag, String log, Throwable e) {
        android.util.Log.wtf(LOGTAG, getLogMsg(tag, log), e);
    }

    private static String getLogMsg(String tag, String log) {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(DateUtil.convertTime(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss")); // SimpleDateFormat不是线程安全的
        if (!TextUtils.isEmpty(tag)) {
            stringBuilder.append("==[").append(tag).append("]==  ");
        }
        if (log != null) {
            stringBuilder.append(log);
        }
        return stringBuilder.toString();
    }

    public static void printStackTrace(String tag, Throwable tr) {
        if (isDebug) {
            android.util.Log.e(tag,
                    android.util.Log.getStackTraceString(tr));
        }
    }

    public static void printStackTrace(String tag) {
        printStackTrace(tag, new Throwable());
    }

    public static void printStackTraceInfo(String tag) {
        if (isDebug) {
            StackTraceElement[] tStackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement tTraceElement : tStackTraceElements) {
                android.util.Log.i(tag, tTraceElement.toString());
            }
        }
    }

    private static boolean sRecordingLog = true;

    public static void stopRecordingLog() {
        sRecordingLog = false;
    }

    public static void writeLogToFile() {
        Log.w(LOGTAG, "dwz-->writeLogToFile(), 1111");
        AsyncOperationUtil.asyncOperation(new Runnable() {
            @Override
            public void run() {
                Log.w(LOGTAG, "dwz-->writeLogToFile()");
                try {
                    ArrayList<String> getLog = new ArrayList<String>();
                    getLog.add("logcat");
                    getLog.add("-v");
                    getLog.add("time");
                    ArrayList<String> clearLog = new ArrayList<String>();
                    clearLog.add("logcat");
                    clearLog.add("-c");
                    File externalStorage = Environment.getExternalStorageDirectory();
                    File logFolder = new File(externalStorage, "1loading");
                    if (!logFolder.exists()) {
                        logFolder.mkdirs();
                    }
                    //获取logcat输出
                    Process logcatProcess = Runtime.getRuntime().exec(getLog.toArray(new String[getLog.size()]));
                    BufferedReader buffRead = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));

                    Log.d(LOGTAG, "-->writeLogToFile(): log folder path=" + logFolder.getAbsolutePath()
                            + ", logFolder exist?" + logFolder.exists());

                    int logFileIndex = 0;
                    int maxValidSuffix = LOG_FILE_MAX_CNT - 1;

                    while (sRecordingLog) {
                        int logFileSuffix = logFileIndex;
                        /**
                         * 0 1 2 3 4 5 6 7 8 9 10 ...
                         * ==>
                         * 0 1 2 3 4 1 2 3 4 1 2
                         */
                        if (logFileSuffix > maxValidSuffix) {
                            logFileSuffix = logFileSuffix % maxValidSuffix;
                            if (logFileSuffix == 0) {
                                logFileSuffix = maxValidSuffix;
                            }
                        }
                        Log.d(LOGTAG, "create new log file, index=" + logFileIndex + ", suffix=" + logFileSuffix);
                        String logFileName = "log." + logFileSuffix;

                        File logFile = new File(logFolder, logFileName);
                        if (logFile.exists()) {
                            logFile.delete();
                        } else {
                            logFile.createNewFile();
                        }
                        String str;
                        FileOutputStream fos = new FileOutputStream(logFile, true);
                        String newline = System.getProperty("line.separator");
                        long logCount = 0;

                        StringBuffer logFileContPrefix = new StringBuffer();
                        logFileContPrefix.append("========= Begin of log file ").append(logFileName).append("================").append(newline);
                        fos.write(logFileContPrefix.toString().getBytes());
                        while ((str = buffRead.readLine()) != null) {
                            fos.write(str.getBytes());
                            fos.write(newline.getBytes());
                            logCount++;
                            if (logCount > LOG_FILE_LINE_CNT) {
                                fos.close();
                                break;
                            }
                        }
                        fos.close();
                        logFileIndex++;
                    }
//                    Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
