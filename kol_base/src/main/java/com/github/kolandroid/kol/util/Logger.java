package com.github.kolandroid.kol.util;

public abstract class Logger {
    private static Logger primary_logger = new Logger() {
        @Override
        public void do_log(String tag, String message) {
            System.out.println(tag + ": " + message);
        }
    };

    public static void log(String tag, String message) {
        primary_logger.do_log(tag, message);
    }

    public static void override_logger(Logger new_logger) {
        primary_logger = new_logger;
    }

    public abstract void do_log(String tag, String message);
}
