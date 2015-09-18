package com.github.kolandroid.kol.android.login;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.login.ConnectionFailed;
import com.github.kolandroid.kol.model.models.login.LoginConnectingModel;

public class LoginConnectingController extends LinkedModelController<ConnectionFailed, LoginConnectingModel> {
    public LoginConnectingController() {
        super(new LoginConnectingModel());
    }

    @Override
    public void receiveProgress(View view, LoginConnectingModel model, ConnectionFailed failure, Screen host) {
        TextView displayMessage = (TextView) view.findViewById(R.id.login_connecting_message);
        displayMessage.setText(failure.getReason());
    }

    @Override
    public void connect(View view, LoginConnectingModel model, Screen host) {
        TextView displayMessage = (TextView) view.findViewById(R.id.login_connecting_message);
        displayMessage.setText("Connecting!");

        model.doLogin(host.getViewContext());
    }

    @Override
    public int getView() {
        return R.layout.fragment_login_connecting;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayExternal(this);
    }
}
