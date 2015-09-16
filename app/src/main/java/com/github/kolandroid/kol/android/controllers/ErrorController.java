package com.github.kolandroid.kol.android.controllers;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.ErrorModel;

public class ErrorController extends ModelController<ErrorModel> {
    public ErrorController(String message, boolean severe) {
        this(new ErrorModel(message, severe));
    }

    public ErrorController(ErrorModel model) {
        super(model);
    }

    @Override
    public int getView() {
        return R.layout.dialog_error_screen;
    }

    @Override
    public void connect(View view, ErrorModel model, Screen host) {
        TextView txtMessage = (TextView) view.findViewById(R.id.error_info);
        txtMessage.setText(model.getMessage());
    }

    @Override
    public void disconnect(Screen host) {
        //do nothing
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        if (getModel().isSevere())
            choice.displayExternalDialog(this);
        else
            choice.displayDialog(this);
    }

}
