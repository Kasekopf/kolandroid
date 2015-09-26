package com.github.kolandroid.kol.android.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.login.ExpiringController;
import com.github.kolandroid.kol.android.login.LoginConnectingController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.ErrorReportingModel;
import com.github.kolandroid.kol.util.Logger;

public class ErrorReportingController extends ModelController<ErrorReportingModel> implements ExpiringController {
    private boolean stale;

    public ErrorReportingController(ErrorReportingModel model) {
        super(model);
    }

    @Override
    public void attach(View view, ErrorReportingModel model, final Screen host) {
        final EditText text = (EditText) view.findViewById(R.id.error_reporting_description);

        stale = false;
        Button submit = (Button) view.findViewById(R.id.error_reporting_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = text.getText().toString();
                String report = getModel().generateReport(description);
                String reportTitle = getModel().generateReportTitle();
                String reportAddress = getModel().generateReportAddress();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{reportAddress});
                intent.putExtra(Intent.EXTRA_SUBJECT, reportTitle);
                intent.putExtra(Intent.EXTRA_TEXT, report);

                Activity baseActivity = host.getActivity();
                if (baseActivity != null && intent.resolveActivity(baseActivity.getPackageManager()) != null) {
                    Logger.log("ErrorReportingController", "Sending intent with error email");
                    getModel().clearReport();
                    stale = true;
                    baseActivity.startActivity(intent);
                }
            }
        });

        Button cancel = (Button) view.findViewById(R.id.error_reporting_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stale = true;
                // Connect to the login page as usual
                getModel().clearReport();
                host.getViewContext().getPrimaryRoute().execute(new LoginConnectingController());
            }
        });
    }

    @Override
    public int getView() {
        return R.layout.error_reporting_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayExternal(this);
    }

    @Override
    public boolean hasExpired() {
        return stale;
    }
}
