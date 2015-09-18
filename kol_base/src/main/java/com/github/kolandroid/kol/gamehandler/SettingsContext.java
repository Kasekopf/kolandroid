package com.github.kolandroid.kol.gamehandler;

public interface SettingsContext {
    boolean contains(String name);

    void remove(String name);

    boolean get(String name, boolean defaultValue);

    int get(String name, int defaultValue);

    String get(String name, String defaultValue);

    void set(String name, boolean value);

    void set(String name, int value);

    void set(String name, String value);
}
