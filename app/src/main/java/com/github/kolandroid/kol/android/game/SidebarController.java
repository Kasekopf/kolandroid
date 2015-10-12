package com.github.kolandroid.kol.android.game;

import android.view.View;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.navigation.NavigationController;
import com.github.kolandroid.kol.android.controllers.stats.StatsOverviewController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.screen.ViewScreen;
import com.github.kolandroid.kol.model.models.navigation.NavigationModel;
import com.github.kolandroid.kol.model.models.stats.StatsOverviewModel;
import com.github.kolandroid.kol.session.Session;

public class SidebarController implements Controller {
    private StatsOverviewController stats;
    private NavigationController navigation;

    private transient ViewScreen statsScreen;
    private transient ViewScreen navScreen;

    public SidebarController(Session session) {
        stats = new StatsOverviewController(new StatsOverviewModel(session));
        navigation = new NavigationController(new NavigationModel(session));
    }

    public void refresh() {
        stats.refresh();
    }

    @Override
    public int getView() {
        return R.layout.sidebar_view;
    }

    @Override
    public void attach(View view, Screen host) {
        statsScreen = (ViewScreen) view.findViewById(R.id.sidebar_stats);
        statsScreen.attach(stats, host);

        navScreen = (ViewScreen) view.findViewById(R.id.sidebar_navigation);
        navScreen.attach(navigation, host);
    }

    @Override
    public void connect(View view, Screen host) {
        if (statsScreen != null) {
            statsScreen.connect();
        }

        if (navScreen != null) {
            navScreen.connect();
        }
    }

    @Override
    public void disconnect(Screen host) {
        if (statsScreen != null) {
            statsScreen.disconnect();
        }

        if (navScreen != null) {
            navScreen.disconnect();
        }
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        // Do nothing
    }
}
