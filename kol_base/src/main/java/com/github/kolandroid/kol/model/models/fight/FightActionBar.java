package com.github.kolandroid.kol.model.models.fight;

import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.model.elements.interfaces.ModelGroup;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.SessionCache;
import com.github.kolandroid.kol.session.data.FightActionBarData;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;

public class FightActionBar extends LinkedModel<Void> {
    private final FightModel parent;
    private int startingPage = 0;
    private ArrayList<ArrayList<FightAction>> pages;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public FightActionBar(Session s, ViewContext host, FightModel parent) {
        super(s);

        this.pages = new ArrayList<>();
        this.parent = parent;
        final SettingsContext settings = host.getSettingsContext();
        SessionCache cache = host.getDataContext().getSessionCache(s);
        cache.access(FightActionBarData.class, new Callback<FightActionBarData>() {
            @Override
            public void execute(FightActionBarData item) {
                process(item, settings);
                notifyView(null);
            }
        }, new Callback<Void>() {
            @Override
            public void execute(Void item) {
                // Do nothing
            }
        });
    }

    public int getStartingPage() {
        return startingPage;
    }

    public ArrayList<ArrayList<FightAction>> getPages() {
        return pages;
    }

    private void process(FightActionBarData data, SettingsContext settings) {
        ArrayList<ArrayList<FightAction>> pages = new ArrayList<>();

        ArrayList<ModelGroup<FightItem>> items = parent.getItems();
        ArrayList<ModelGroup<FightAction>> skills = parent.getSkills();

        this.startingPage = settings.get("fight_actionbarpage", data.getCurrentPage());
        for (FightActionBarData.FightActionBarRawElementData[] page : data.getPages()) {
            ArrayList<FightAction> newPage = new ArrayList<>();
            for (FightActionBarData.FightActionBarRawElementData element : page) {
                if (element == null || element.id == null || element.type == null) {
                    newPage.add(FightAction.NONE);
                } else {
                    boolean found = false;
                    switch (element.type) {
                        case "item":
                            // Look through all current items to find a match
                            for (ModelGroup<FightItem> itemGroup : items) {
                                for (FightAction item : itemGroup) {
                                    if (item.matchesActionBarItem(element.type, element.id)) {
                                        newPage.add(item);
                                        found = true;
                                        break;
                                    }
                                }

                                if (found) break;
                            }

                            if (!found) {
                                // No match was found
                                newPage.add(new DisabledFightAction(element.type, element.id, element.pic));
                            }
                            break;
                        case "skill":
                        case "action":
                            // Look through all current skills to find a match
                            for (ModelGroup<FightAction> skillGroup : skills) {
                                for (FightAction skill : skillGroup) {
                                    if (skill.matchesActionBarItem(element.type, element.id)) {
                                        newPage.add(skill);
                                        found = true;
                                        break;
                                    }
                                }

                                if (found) break;
                            }

                            // Compare against the default attack action as well
                            if (element.id != null && element.id.equals("attack")) {
                                newPage.add(new FightBasicAction(getSession(), "Attack", "http://images.kingdomofloathing.com/itemimages/" + element.pic + ".gif", "attack", FightActionHistory.NONE));
                                found = true;
                            }

                            if (!found) {
                                // No match was found
                                newPage.add(new DisabledFightAction(element.type, element.id, element.pic));
                            }
                            break;
                        default:
                            Logger.log("FightActionBar", "Unknown combat action type: " + element.type);
                            newPage.add(FightAction.NONE);
                    }
                }
            }

            pages.add(newPage);
        }

        this.pages = pages;
        notifyView(null);
    }
}
