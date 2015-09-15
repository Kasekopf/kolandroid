package com.github.kolandroid.kol.android.controllers;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;

public class ErrorController implements Controller {
    final String message;

    public ErrorController(String message) {
        this.message = message;
    }

    @Override
    public int getView() {
        return R.layout.dialog_error_screen;
    }

    @Override
    public void connect(View view, Screen host) {
        TextView txtMessage = (TextView) view.findViewById(R.id.error_info);
        txtMessage.setText(message);
    }

    @Override
    public void disconnect(Screen host) {
        //do nothing
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
