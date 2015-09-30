package com.github.kolandroid.kol.android.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.model.ReportableMessageModel;
import com.github.kolandroid.kol.util.Logger;

public class ReportableMessageController extends MessageController<ReportableMessageModel> {

    public ReportableMessageController(ReportableMessageModel model) {
        super(model);
    }

    @Override
    public void attach(View view, ReportableMessageModel model, final Screen host) {
        super.attach(view, model, host);

        Button submit = (Button) view.findViewById(R.id.message_submit_error);
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String report = getModel().generateReport();
                String reportTitle = getModel().generateReportTitle();
                String reportAddress = getModel().generateReportAddress();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{reportAddress});
                intent.putExtra(Intent.EXTRA_SUBJECT, reportTitle);
                intent.putExtra(Intent.EXTRA_TEXT, report);

                Activity baseActivity = host.getActivity();
                if (baseActivity != null && intent.resolveActivity(baseActivity.getPackageManager()) != null) {
                    Logger.log("ReportableMessageController", "Sending intent with error email");
                    host.close();
                    baseActivity.startActivity(intent);
                }
            }
        });
    }
}
