package com.github.kolandroid.kol.android.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.android.util.ProgressBar;
import com.github.kolandroid.kol.android.view.ProgressBarLoader;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.model.models.login.AppUpdaterModel;
import com.github.kolandroid.kol.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class AppUpdaterController extends ModelController<AppUpdaterModel> {
    private static final String fileUrl = "kolandroid_update.apk";
    private final boolean showAutoCheckbox;
    private transient HandlerCallback<Boolean> onFinishDownloading;

    public AppUpdaterController(AppUpdaterModel model) {
        this(model, true);
    }

    public AppUpdaterController(AppUpdaterModel model, boolean showAutoCheckbox) {
        super(model);
        this.showAutoCheckbox = showAutoCheckbox;
    }

    @Override
    public void attach(View view, AppUpdaterModel model, final Screen host) {
        final SettingsContext settings = host.getViewContext().getSettingsContext();

        CheckBox autoUpdate = (CheckBox) view.findViewById(R.id.app_updater_automatic);
        autoUpdate.setChecked(settings.get("update_automatically", true));
        autoUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.set("update_automatically", isChecked);
            }
        });
        autoUpdate.setVisibility(showAutoCheckbox ? View.VISIBLE : View.GONE);
    }

    @Override
    public void connect(View view, AppUpdaterModel model, final Screen host) {
        onFinishDownloading = new HandlerCallback<Boolean>() {
            @Override
            protected void receiveProgress(Boolean message) {
                Logger.log("AppUpdaterController", "Download Complete!");

                //Launch the .apk within an intent to trigger the installer
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File apkFile = host.getActivity().getFileStreamPath(fileUrl);
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                host.getActivity().startActivity(intent);

                host.close();
            }
        };

        final ProgressBar progress = (ProgressBar) view.findViewById(R.id.app_updater_progress);
        final ProgressBarLoader loader = new ProgressBarLoader(progress, host, onFinishDownloading.weak());

        final Button download = (Button) view.findViewById(R.id.app_updater_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream file = host.getActivity().openFileOutput(fileUrl, Context.MODE_WORLD_READABLE);
                    download.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                    getModel().downloadApp(file, loader);
                } catch (FileNotFoundException e) {
                    Logger.log("AppUpdaterController", "Unable to open file");
                    return;
                }
            }
        });
    }

    @Override
    public void disconnect(Screen host) {
        onFinishDownloading.close();
    }

    @Override
    public int getView() {
        return R.layout.app_updater_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
