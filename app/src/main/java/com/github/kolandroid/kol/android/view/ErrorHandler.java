package com.github.kolandroid.kol.android.view;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.github.kolandroid.kol.android.BuildConfig;
import com.github.kolandroid.kol.model.models.ErrorReportingModel;
import com.github.kolandroid.kol.util.Logger;

public class ErrorHandler implements Thread.UncaughtExceptionHandler {
    private final AndroidSettingsContext settings;

    public ErrorHandler(Context context) {
        this.settings = new AndroidSettingsContext(context);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Logger.log("ErrorHandler", "Uncaught exception: " + ex);

        String error = "";

        error += "AppVersion: " + BuildConfig.VERSION_CODE + "\n";
        error += "AndroidVersion: " + Build.VERSION.RELEASE + "\n";
        error += "PhoneModel: " + Build.MODEL + "\n";
        error += "PhoneManufacturer: " + Build.MANUFACTURER + "\n";
        error += "StackTrace: " + Log.getStackTraceString(ex);
        settings.setImmediately(ErrorReportingModel.ERROR_SETTING, error);
        System.exit(2);
    }
}
