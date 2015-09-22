package com.github.kolandroid.kol.gamehandler;

public interface SettingsContext {
    SettingsContext NONE = new SettingsContext() {
        @Override
        public boolean contains(String name) {
            return false;
        }

        @Override
        public void remove(String name) {

        }

        @Override
        public boolean get(String name, boolean defaultValue) {
            return defaultValue;
        }

        @Override
        public int get(String name, int defaultValue) {
            return defaultValue;
        }

        @Override
        public String get(String name, String defaultValue) {
            return defaultValue;
        }

        @Override
        public void set(String name, boolean value) {

        }

        @Override
        public void set(String name, int value) {

        }

        @Override
        public void set(String name, String value) {

        }
    };

    boolean contains(String name);

    void remove(String name);

    boolean get(String name, boolean defaultValue);

    int get(String name, int defaultValue);

    String get(String name, String defaultValue);

    void set(String name, boolean value);

    void set(String name, int value);

    void set(String name, String value);
}
