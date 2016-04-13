package com.github.kolandroid.kol.android.view;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.util.Logger;

import java.util.Set;

public class AndroidSettingsContext implements SettingsContext {
    private static final String GLOBAL_STORAGE = "KoL_Global_Storage";

    private final SharedPreferences globalSettings;

    public AndroidSettingsContext(Context c) {
        this.globalSettings = c.getSharedPreferences(GLOBAL_STORAGE, Context.MODE_PRIVATE);
    }

    public boolean contains(String name) {
        return globalSettings.contains(name);
    }

    public void remove(String name) {
        Logger.log("AndroidSettingsContext", "[" + name + "] removed");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.remove(name);
        editor.apply();
    }

    @Override
    public boolean get(String name, boolean defaultValue) {
        try {
            return globalSettings.getBoolean(name, defaultValue);
        } catch (ClassCastException e) {
            Logger.log("AndroidSettingsContext", "[" + name + "] expected boolean");
            return defaultValue;
        }
    }

    @Override
    public int get(String name, int defaultValue) {
        try {
            return globalSettings.getInt(name, defaultValue);
        } catch (ClassCastException e) {
            Logger.log("AndroidSettingsContext", "[" + name + "] expected integer");
            return defaultValue;
        }
    }

    @Override
    public String get(String name, String defaultValue) {
        try {
            return globalSettings.getString(name, defaultValue);
        } catch (ClassCastException e) {
            Logger.log("AndroidSettingsContext", "[" + name + "] expected string");
            return defaultValue;
        }
    }

    @Override
    public Set<String> get(String name, Set<String> defaultValue) {
        try {
            return globalSettings.getStringSet(name, defaultValue);
        } catch (ClassCastException e) {
            Logger.log("AndroidSettingsContext", "[" + name + "] expected string set");
            return defaultValue;
        }
    }

    @Override
    public void set(String name, boolean value) {
        //Logger.log("AndroidSettingsContext", "[" + name + "] set to $boolean[" + value + "]");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    @Override
    public void set(String name, int value) {
        //Logger.log("AndroidSettingsContext", "[" + name + "] set to $int[" + value + "]");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    @Override
    public void set(String name, String value) {
        //Logger.log("AndroidSettingsContext", "[" + name + "] set to $string[" + value + "]");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.putString(name, value);
        editor.apply();
    }

    @Override
    public void set(String name, Set<String> value) {
        // Join the string values for the sake of logging
        /*
        StringBuilder logentry = new StringBuilder("[").append(name).append("] set to $strings[");
        for (String s : value) {
            logentry.append(s).append(", ");
        }
        logentry.append("]");

        Logger.log("AndroidSettingsContext", logentry.toString());
        */
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.putStringSet(name, value);
        editor.apply();
    }

    /**
     * Normally, settings are slightly asynchronously applied.
     * This commits the settings change immediately.
     *
     * @param name
     * @param value
     */
    public void setImmediately(String name, String value) {
        //Logger.log("AndroidSettingsContext", "[" + name + "] set to $string[" + value + "]");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.putString(name, value);
        editor.commit();

    }
}
