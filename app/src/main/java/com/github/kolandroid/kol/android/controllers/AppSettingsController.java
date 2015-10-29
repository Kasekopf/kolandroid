package com.github.kolandroid.kol.android.controllers;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.kolandroid.kol.android.BuildConfig;
import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.model.models.AppSettingsModel;
import com.github.kolandroid.kol.model.models.login.AppUpdaterModel;

public class AppSettingsController extends ModelController<AppSettingsModel> {
    private transient HandlerCallback onUpdateCheck;

    public AppSettingsController(AppSettingsModel model) {
        super(model);
    }

    @Override
    public void attach(View view, AppSettingsModel model, Screen host) {
        TextView version = (TextView) view.findViewById(R.id.app_settings_version);
        version.setText("Version " + BuildConfig.VERSION_NAME);

        final SettingsContext settings = host.getViewContext().getSettingsContext();
        CheckBox autoUpdate = (CheckBox) view.findViewById(R.id.app_settings_autoupdate);
        autoUpdate.setChecked(settings.get("update_automatically", true));
        autoUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.set("update_automatically", isChecked);
            }
        });

    }

    @Override
    public void connect(View view, AppSettingsModel model, final Screen host) {
        final Button checkUpdates = (Button) view.findViewById(R.id.app_settings_update);

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

    public void disconnect(Screen host) {
        onUpdateCheck.close();
    }

    @Override
    public int getView() {
        return R.layout.app_settings_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
