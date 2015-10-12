package com.github.kolandroid.kol.android.controllers.stats;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.ProgressBar;
import com.github.kolandroid.kol.model.models.stats.StatsGlanceModel;
import com.github.kolandroid.kol.util.Callback;

import java.text.NumberFormat;

public class StatsGlanceController extends LinkedModelController<Void, StatsGlanceModel> {
    private transient Callback<Void> onUpdate;

    public StatsGlanceController(StatsGlanceModel model) {
        super(model);
    }

    public void attachNotificationCallback(Callback<Void> onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    public void receiveProgress(View view, StatsGlanceModel model, Void message, Screen host) {
        if (onUpdate != null) {
            onUpdate.execute(null);
        }

        ProgressBar barHP = (ProgressBar) view.findViewById(R.id.stats_hp);
        barHP.setProgress(model.getCurrentHP(), model.getMaxHP());

        ProgressBar barMP = (ProgressBar) view.findViewById(R.id.stats_mp);
        barMP.setProgress(model.getCurrentMP(), model.getMaxMP());

        NumberFormat formatter = NumberFormat.getIntegerInstance();
        TextView txtAdv = (TextView) view.findViewById(R.id.stats_adventures);
        txtAdv.setText("  " + formatter.format(model.getAdventures()));

        TextView txtMeat = (TextView) view.findViewById(R.id.stats_meat);
        txtMeat.setText("  " + formatter.format(model.getMeat()));
    }

    public void refresh() {
        getModel().update();
    }

    @Override
    public void attach(View view, StatsGlanceModel model, Screen host) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getModel().displayFull();
            }
        });

        this.refresh();
    }

    @Override
    public int getView() {
        return R.layout.stats_glance_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        // Do nothing
    }
}
