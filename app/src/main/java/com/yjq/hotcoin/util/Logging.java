package com.yjq.hotcoin.util;

import android.util.Log;

/**
 * Log打印（控制研发版本和正式版本的日志打印）
 */
public class Logging {
    private static boolean logable = true;
    private static int logLevel;

    /**
     * 日志等级枚举
     */
    public static enum LogPriority {
        VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT
    }

    /**
     * 同Log.v
     *
     * @param tag
     * @param msg
     * @return
     */
    public static int v(String tag, String msg) {
        return println(LogPriority.VERBOSE, tag, msg);
    }

    /**
     * 同Log.d
     *
     * @param tag
     * @param msg
     * @return
     */
    public static int d(String tag, String msg) {
        return println(LogPriority.DEBUG, tag, msg);
    }

    /**
     * 同Log.i
     *
     * @param tag
     * @param msg
     * @return
     */
    public static int i(String tag, String msg) {
        return println(LogPriority.INFO, tag, msg);
    }

    /**
     * 同Log.w
     *
     * @param tag
     * @param msg
     * @return
     */
    public static int w(String tag, String msg) {
        return println(LogPriority.WARN, tag, msg);
    }

    /**
     * 同Log.e
     *
     * @param tag
     * @param msg
     * @return
     */
    public static int e(String tag, String msg) {
        return println(LogPriority.ERROR, tag, msg);
    }

    /**
     * 获取是否可打印日志
     *
     * @return
     */
    public static boolean isLogable() {
        return logable;
    }

    /**
     * 设置是否打印日志
     *
     * @param logable
     */
    public static void setLogable(boolean logable) {
        Logging.logable = logable;
    }

    /**
     * 设置日志等级
     *
     * @param level
     */
    public static void setLogLevel(LogPriority level) {
        Logging.logLevel = level.ordinal();
    }

    private static int println(LogPriority priority, String tag, String msg) {
        int level = priority.ordinal();
        if (logable && logLevel <= level) {
            return Log.println(level + 2, tag, msg);
        } else {
            return -1;
        }
    }
}
