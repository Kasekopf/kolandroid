package com.github.kolandroid.kol.android.screen;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controller.UpdateController;

public interface ScreenSelection {
    void displayExternal(Controller c);

    void displayExternalDialog(Controller c, boolean cancellable);

    void displayPrimary(Controller c);

    void displayPrimaryUpdate(UpdateController c, boolean displayIfUnable);

    void displayDialog(Controller c);

    void displayChat(Controller c);
}
