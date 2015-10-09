package com.github.kolandroid.kol.android.controllers.fight;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.adapters.PagerControllerAdapter;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.model.models.fight.FightAction;
import com.github.kolandroid.kol.model.models.fight.FightActionBar;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;

public class FightActionBarController extends LinkedModelController<Void, FightActionBar> {
    private final Callback<FightAction> notifyActionSelection;
    private transient PagerControllerAdapter<FightActionBarPageController> adapter;

    public FightActionBarController(FightActionBar model, Callback<FightAction> notifyActionSelection) {
        super(model);
        this.notifyActionSelection = notifyActionSelection;
    }

    private ArrayList<FightActionBarPageController> constructControllers() {
        ArrayList<FightActionBarPageController> controllers = new ArrayList<>();
        for (ArrayList<FightAction> page : getModel().getPages()) {
            controllers.add(new FightActionBarPageController(page, notifyActionSelection));
        }
        Logger.log("FightActionBarController", "Loaded " + controllers.size() + " pages");
        return controllers;
    }

    @Override
    public void receiveProgress(View view, FightActionBar model, Void message, Screen host) {
        if (adapter != null) {
            adapter.setElements(constructControllers());
        }

        //Recall the last selected page
        SettingsContext settings = host.getViewContext().getSettingsContext();
        ((ViewPager) view).setCurrentItem(settings.get("FightActionBarController:SelectedPage", model.getStartingPage()));
        view.requestLayout();
    }

    @Override
    public void attach(View view, FightActionBar model, final Screen host) {
        adapter = new PagerControllerAdapter<>(host, constructControllers());
        ((ViewPager) view).setAdapter(adapter);
        view.requestLayout();
        view.invalidate();

        //Recall the last selected page
        SettingsContext settings = host.getViewContext().getSettingsContext();
        ((ViewPager) view).setCurrentItem(settings.get("FightActionBarController:SelectedPage", model.getStartingPage()));

        //Remember the most recent selected page
        ((ViewPager) view).addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Do nothing
            }

            @Override
            public void onPageSelected(int position) {
                // Remember the most recent page selected
                SettingsContext settings = host.getViewContext().getSettingsContext();
                settings.set("FightActionBarController:SelectedPage", position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }
        });
    }

    @Override
    public int getView() {
        return 0;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {

    }
}
