package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.model.Model;

public class ErrorReportingModel extends Model {
    public static final String ERROR_SETTING = "AppErrorInfo";
    private final String errorLog;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public ErrorReportingModel(SettingsContext settings) {
        super(new Session());

        this.errorLog = settings.get(ERROR_SETTING, "");
    }

    public static boolean detectError(SettingsContext settings) {
        return settings.contains(ERROR_SETTING);
    }

    public String generateReportTitle() {
        return "KoL Android Crash Report";
    }

    public String generateReportAddress() {
        return "kingdomofloathingandroid@gmail.com";
    }

    public String generateReport(String userDescription) {
        return "Description: " + userDescription + "\n\n" + this.errorLog;
    }

    public void clearReport() {
        this.getSettings().remove(ERROR_SETTING);
    }
}
