package com.github.kolandroid.kol.android.view;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.util.Logger;

public class AndroidSettingsContext implements SettingsContext {
    private static final String GLOBAL_STORAGE = "KoL_Global_Storage";

    private SharedPreferences globalSettings;

    public AndroidSettingsContext(Context c) {
        this.globalSettings = c.getSharedPreferences(GLOBAL_STORAGE, 0);
    }

    public boolean contains(String name) {
        return globalSettings.contains(name);
    }

    public void remove(String name) {
        Logger.log("AndroidSettingsContext", "[" + name + "] removed");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.remove(name);
        editor.commit();
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
    public void set(String name, boolean value) {
        Logger.log("AndroidSettingsContext", "[" + name + "] set to $boolean[" + value + "]");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    @Override
    public void set(String name, int value) {
        Logger.log("AndroidSettingsContext", "[" + name + "] set to $int[" + value + "]");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    @Override
    public void set(String name, String value) {
        Logger.log("AndroidSettingsContext", "[" + name + "] set to $string[" + value + "]");
        SharedPreferences.Editor editor = globalSettings.edit();
        editor.putString(name, value);
        editor.commit();
    }
}
