package com.github.kolandroid.kol.android.controller;

import android.view.View;

import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;

import java.io.Serializable;

public interface Controller extends Serializable {
    /**
     * Get the layout to create when hosting this controller.
     *
     * @return A layout id to be inflated.
     */
    int getView();

    /**
     * Attach the provided view to this controller.
     *
     * @param view The view to attach
     * @param host The screen acting as host for the controller
     */
    void attach(View view, Screen host);

    /**
     * Connect any additional services/callbacks.
     * This will be called after attach. After any disconnect,
     * connect will be called again before anything is displayed
     * to the user.
     *
     * @param view  The attached view
     * @param host  The screen acting as host for the controller
     */
    void connect(View view, Screen host);

    /**
     * Disconnect any additional services/callbacks
     * @param host  The screen acting as host for the controller
     */
    void disconnect(Screen host);

    /**
     * Request a specific screen to display this controller
     * @param choice    A visitor used to select a screen.
     */
    void chooseScreen(ScreenSelection choice);
}
