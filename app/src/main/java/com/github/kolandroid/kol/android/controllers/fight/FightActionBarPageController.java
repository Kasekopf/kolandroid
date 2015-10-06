package com.github.kolandroid.kol.android.controllers.fight;

import android.view.View;
import android.widget.ImageView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.ImageDownloader;
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
        int[] ids = {R.id.fight_actionbar_1, R.id.fight_actionbar_2, R.id.fight_actionbar_3, R.id.fight_actionbar_4, R.id.fight_actionbar_5, R.id.fight_actionbar_6, R.id.fight_actionbar_7, R.id.fight_actionbar_8, R.id.fight_actionbar_9, R.id.fight_actionbar_10, R.id.fight_actionbar_11, R.id.fight_actionbar_12};
        for (int i = 0; i < ids.length && i < page.size(); i++) {
            ImageView image = (ImageView) view.findViewById(ids[i]);
            final FightAction action = page.get(i);
            ImageDownloader.loadFromUrl(image, action.getImage());
        }
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
