package com.github.kolandroid.kol.util;

public abstract class Logger {
    private static final int MAX_MESSAGE_SIZE = 4000;

    private static Logger primary_logger = new Logger() {
        @Override
        public void do_log(String tag, String message) {
            System.out.println(tag + ": " + message);
        }
    };

    public static void log(String tag, String message) {
        if (message == null)
            message = "[NULL]";
        primary_logger.do_log(tag, message);
    }

    public static void logBig(String tag, String message) {
        int length = message.length();
        int total = (length + MAX_MESSAGE_SIZE - 1) / MAX_MESSAGE_SIZE;
        for (int i = 0; i < total; i++) {
            int start = i * MAX_MESSAGE_SIZE;
            int end = (i + 1) * MAX_MESSAGE_SIZE;
            if (end > length) end = length;
            log(tag + "(" + (i + 1) + "/" + total + ")", message.substring(start, end));
        }
    }

    public static void override_logger(Logger new_logger) {
        primary_logger = new_logger;
    }

    public abstract void do_log(String tag, String message);
}
