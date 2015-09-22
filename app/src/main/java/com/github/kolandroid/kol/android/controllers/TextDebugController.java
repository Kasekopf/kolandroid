package com.github.kolandroid.kol.android.controllers;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.WebModel;

public class TextDebugController extends ModelController<WebModel> {
    public TextDebugController(WebModel model) {
        super(model);
    }

    @Override
    public int getView() {
        return R.layout.fragment_text_screen;
    }

    @Override
    public void connect(View view, WebModel model, Screen host) {
        TextView text = (TextView) view.findViewById(R.id.textdebug_text);
        text.setText(model.getHTML());
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayPrimary(this, false);
    }
}
