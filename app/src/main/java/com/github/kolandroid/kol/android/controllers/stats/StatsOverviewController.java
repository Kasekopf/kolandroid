package com.github.kolandroid.kol.android.controllers.stats;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.ImageDownloader;
import com.github.kolandroid.kol.model.models.stats.StatsOverviewModel;

public class StatsOverviewController extends LinkedModelController<Void, StatsOverviewModel> {
    public StatsOverviewController(StatsOverviewModel model) {
        super(model);
    }

    @Override
    public void receiveProgress(View view, StatsOverviewModel model, Void message, Screen host) {
        TextView name = (TextView) view.findViewById(R.id.stats_overview_name);
        name.setText(model.getName());

        TextView title = (TextView) view.findViewById(R.id.stats_overview_title);
        title.setText(model.getTitle());

        ImageView avatar = (ImageView) view.findViewById(R.id.stats_overview_avatar);
        ImageDownloader.loadFromUrl(avatar, "images.kingdomofloathing.com/otherimages/hippycostume.gif");

        TextView musc = (TextView) view.findViewById(R.id.stats_overview_musc);
        musc.setText(Html.fromHtml(model.getMuscle()));
        TextView myst = (TextView) view.findViewById(R.id.stats_overview_myst);
        myst.setText(Html.fromHtml(model.getMyst()));
        TextView moxie = (TextView) view.findViewById(R.id.stats_overview_moxie);
        moxie.setText(Html.fromHtml(model.getMoxie()));
    }

    public void refresh() {
        getModel().update();
    }

    @Override
    public void attach(View view, StatsOverviewModel model, final Screen host) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getModel().displayFull();
                host.close();
            }
        });
        this.refresh();
    }

    @Override
    public int getView() {
        return R.layout.stats_overview_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        // Do nothing
    }
}
