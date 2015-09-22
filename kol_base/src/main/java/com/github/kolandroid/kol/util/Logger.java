package com.github.kolandroid.kol.util;

/**
 * A static logger, which is injected with the android logger at runtime.
 */
public abstract class Logger {
    //The maximum message size the log can support.
    private static final int MAX_MESSAGE_SIZE = 4000;

    //The logger to use for static calls.
    private static Logger primary_logger = new Logger() {
        @Override
        public void do_log(String tag, String message) {
            System.out.println(tag + ": " + message);
        }
    };

    /**
     * Log the provided message.
     *
     * @param tag     A tag for the message, generally SimpleName of the calling class
     * @param message The message to log
     */
    public static void log(String tag, String message) {
        if (message == null)
            message = "[NULL]";
        primary_logger.do_log(tag, message);
    }

    /**
     * Log the provided message, breaking it into segments if it is too large.
     *
     * @param tag A tag for the message
     * @param message The message to log
     */
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

    /**
     * Inject a new logger to be used.
     *
     * @param new_logger The new logger to use.
     */
    public static void override_logger(Logger new_logger) {
        primary_logger = new_logger;
    }

    /**
     * Actually write the log to some backend sink.
     * @param tag   A tag for the message
     * @param message   The message to log
     */
    public abstract void do_log(String tag, String message);
}
