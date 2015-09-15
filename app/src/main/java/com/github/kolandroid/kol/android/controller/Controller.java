package com.github.kolandroid.kol.android.controller;

import android.view.View;

import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;

import java.io.Serializable;

public interface Controller extends Serializable {
    int getView();

    void connect(View view, Screen host);

    void disconnect(Screen host);

    void chooseScreen(ScreenSelection choice);
}
