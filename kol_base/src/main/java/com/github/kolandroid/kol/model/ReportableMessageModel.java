package com.github.kolandroid.kol.model;

import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.models.MessageModel;

public class ReportableMessageModel extends MessageModel {
    private final String errorLog;

    public ReportableMessageModel(Session s, String title, String message, String actionText, String action, ErrorType error, String errorLog) {
        super(s, title, message, actionText, action, error);

        this.errorLog = errorLog;
    }


    public String generateReportTitle() {
        return "KoL Android Possible Error";
    }

    public String generateReportAddress() {
        return "kingdomofloathingandroid@gmail.com";
    }

    public String generateReport() {
        return this.errorLog;
    }


}
