package com.github.kolandroid.kol.android.controllers.fight;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.adapters.PagerControllerAdapter;
import com.github.kolandroid.kol.model.models.fight.FightAction;
import com.github.kolandroid.kol.model.models.fight.FightActionBar;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;

public class FightActionBarController extends LinkedModelController<Void, FightActionBar> {
    private transient PagerControllerAdapter<FightActionBarPageController> adapter;

    public FightActionBarController(FightActionBar model) {
        super(model);
    }

    private ArrayList<FightActionBarPageController> constructControllers() {
        ArrayList<FightActionBarPageController> controllers = new ArrayList<>();
        for (ArrayList<FightAction> page : getModel().getPages()) {
            controllers.add(new FightActionBarPageController(page));
        }
        Logger.log("FightActionBarController", "Loaded " + controllers.size() + " pages");
        return controllers;
    }

    @Override
    public void receiveProgress(View view, FightActionBar model, Void message, Screen host) {
        adapter.setElements(constructControllers());
        view.requestLayout();
    }

    @Override
    public void attach(View view, FightActionBar model, Screen host) {
        adapter = new PagerControllerAdapter<>(host, constructControllers());
        ((ViewPager) view).setAdapter(adapter);
        view.requestLayout();
        view.invalidate();
    }

    @Override
    public int getView() {
        return 0;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {

    }
}
