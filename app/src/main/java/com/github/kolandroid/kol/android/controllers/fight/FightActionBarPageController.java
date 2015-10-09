package com.github.kolandroid.kol.android.controllers.fight;

import android.graphics.PorterDuff;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.ImageDownloader;
import com.github.kolandroid.kol.model.models.fight.DisabledFightAction;
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

    /**
     * .setBackgroundResource resets the padding for some reason.
     * This sets the background resource of an ImageView without damaging the padding
     *
     * @param image    ImageView to change background of
     * @param resource New background resource to change to
     */
    private static void setImageBackgroundResourceKeepPadding(ImageView image, @DrawableRes int resource) {
        int pL = image.getPaddingLeft();
        int pT = image.getPaddingTop();
        int pR = image.getPaddingRight();
        int pB = image.getPaddingBottom();
        image.setBackgroundResource(resource);
        image.setPadding(pL, pT, pR, pB); //restore the existing padding
    }

    @Override
    public int getView() {
        return R.layout.fight_action_bar_page_view;
    }

    @Override
    public void attach(View view, final Screen host) {
        int[] ids = {R.id.fight_actionbar_1, R.id.fight_actionbar_2, R.id.fight_actionbar_3, R.id.fight_actionbar_4, R.id.fight_actionbar_5, R.id.fight_actionbar_6, R.id.fight_actionbar_7, R.id.fight_actionbar_8, R.id.fight_actionbar_9, R.id.fight_actionbar_10, R.id.fight_actionbar_11, R.id.fight_actionbar_12};
        for (int i = 0; i < ids.length && i < page.size(); i++) {
            final ImageView image = (ImageView) view.findViewById(ids[i]);
            final FightAction action = page.get(i);
            ImageDownloader.loadFromUrl(image, action.getImage());

            if (action instanceof DisabledFightAction) {
                image.setColorFilter(0xFF888888, PorterDuff.Mode.LIGHTEN);
                setImageBackgroundResourceKeepPadding(image, R.drawable.gray_thick_border);
            } else {
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Set the background to light gray and maintain the blue background
                        image.setColorFilter(0xFFCCCCCC, PorterDuff.Mode.MULTIPLY);
                        setImageBackgroundResourceKeepPadding(image, R.drawable.blue_thick_border_gray_background);

                        // Trigger the item/skill/action
                        notifyActionSelection.execute(action);
                    }
                });
                image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        host.getViewContext().displayMessage(action.getText());
                        return true;
                    }
                });
            }
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
