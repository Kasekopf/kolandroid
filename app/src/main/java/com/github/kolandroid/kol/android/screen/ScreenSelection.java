package com.github.kolandroid.kol.android.screen;

import com.github.kolandroid.kol.android.controller.Controller;

public interface ScreenSelection {
    void displayExternal(Controller c);

    void displayPrimary(Controller c);

    void displayDialog(Controller c);
}
