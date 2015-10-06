package com.github.kolandroid.kol.android.controllers.fight;

import android.view.View;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.fight.FightAction;
import com.github.kolandroid.kol.util.Callback;

import java.util.ArrayList;

public class FightActionBarPageController implements Controller {
    private final ArrayList<FightAction> page;

    private final Callback<FightAction> notifyActionSelection;

    public FightActionBarPageController(ArrayList<FightAction> page, Callback<FightAction> notifyActionSelection) {
        this.page = page;
        this.notifyActionSelection = notifyActionSelection;
    }

    @Override
    public int getView() {
        return R.layout.fight_action_bar_page_view;
    }

    @Override
    public void attach(View view, Screen host) {
        // do nothing... for now!
    }

    @Override
    public void connect(View view, Screen host) {
        // Do nothing
    }

    @Override
    public void disconnect(Screen host) {
        // Do nothing
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
