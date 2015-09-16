package com.github.kolandroid.kol.android.screen;

import com.github.kolandroid.kol.android.controller.Controller;

public interface ScreenSelection {
    void displayExternal(Controller c);

    void displayExternalDialog(Controller c);

    void displayPrimary(Controller c, boolean replaceSameType);

    void displayDialog(Controller c);
}
