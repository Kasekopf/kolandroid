package com.github.kolandroid.kol.android.controllers.fight;

import android.view.View;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.fight.FightAction;

import java.util.ArrayList;

public class FightActionBarPageController implements Controller {
    private final ArrayList<FightAction> page;

    public FightActionBarPageController(ArrayList<FightAction> page) {
        this.page = page;
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
