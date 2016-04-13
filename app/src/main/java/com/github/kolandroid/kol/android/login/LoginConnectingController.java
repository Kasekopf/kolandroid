package com.github.kolandroid.kol.android.login;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.controllers.AppUpdaterController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.model.models.login.AppUpdaterModel;
import com.github.kolandroid.kol.model.models.login.ConnectionFailed;
import com.github.kolandroid.kol.model.models.login.LoginConnectingModel;

public class LoginConnectingController extends LinkedModelController<ConnectionFailed, LoginConnectingModel> {
    private transient HandlerCallback onUpdateCheck;

    public LoginConnectingController() {
        super(new LoginConnectingModel());
    }

    @Override
    public void receiveProgress(View view, final LoginConnectingModel model, ConnectionFailed failure, final Screen host) {
        final ProgressBar bar = (ProgressBar) view.findViewById(R.id.login_connecting_progress);
        bar.setVisibility(View.GONE);

        final TextView displayMessage = (TextView) view.findViewById(R.id.login_connecting_message);
        displayMessage.setText(failure.getReason());
        displayMessage.setVisibility(View.VISIBLE);

        final Button retryButton = (Button) view.findViewById(R.id.login_connecting_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                displayMessage.setVisibility(View.GONE);
                model.doLogin(host.getViewContext());
            }
        });
    }

    @Override
    public void attach(View view, LoginConnectingModel model, Screen host) {
        model.doLogin(host.getViewContext());
    }

    @Override
    public void connect(View view, LoginConnectingModel model, final Screen host) {
        super.connect(view, model, host);

        final Button checkUpdates = (Button) view.findViewById(R.id.login_connecting_update_button);

        onUpdateCheck = new HandlerCallback<AppUpdaterModel>() {
            @Override
            protected void receiveProgress(AppUpdaterModel message) {
                if (message == null) return;
                if (message.updateDetected(host.getViewContext().getDataContext())) {
                    AppUpdaterController controller = new AppUpdaterController(message, false);
                    host.close();
                    host.getViewContext().getPrimaryRoute().execute(controller);
                } else {
                    checkUpdates.setText("No Update Found");
                    checkUpdates.setEnabled(false);
                }
            }
        };

        checkUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getModel().checkAppUpdate(onUpdateCheck.weak());
            }
        });
    }

    @Override
    public void disconnect(Screen host) {
        super.disconnect(host);
        onUpdateCheck.close();
    }

    @Override
    public int getView() {
        return R.layout.login_connecting_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayExternalDialog(this, false);
    }
}
