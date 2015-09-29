package com.github.kolandroid.kol.model.models.fight;

import com.github.kolandroid.kol.gamehandler.SettingsContext;

import java.io.Serializable;
import java.util.ArrayList;

public class FightActionHistory<E extends FightAction> implements Serializable {
    private static final int ACTIONS_TO_REMEMBER = 5;
    public static FightActionHistory<FightAction> NONE = new FightActionHistory<FightAction>("") {
        public ArrayList<FightAction> identify(ArrayList<FightAction> fullList, SettingsContext settings) {
            return new ArrayList<>();
        }

        public void store(FightAction action, SettingsContext settings) {
            // do nothing
        }
    };
    private final String name;

    public FightActionHistory(String name) {
        this.name = name;
    }

    public ArrayList<E> identify(ArrayList<E> fullList, SettingsContext settings) {
        ArrayList<E> result = new ArrayList<>();
        if (fullList.size() == 0)
            return result;

        //Attempt to associate an action with each element of the list
        String[] recentlyUsed = settings.get("FightActionHistory:" + name, "").split(";");
        for (String recent : recentlyUsed) {
            for (E action : fullList) {
                if (recent.equals(action.getIdentifier())) {
                    result.add(action);
                    break;
                }
            }
        }

        return result;
    }

    public void store(E action, SettingsContext settings) {
        if (action == null)
            return;

        String newAction = action.getIdentifier().replace(";", "");
        String history = settings.get("FightActionHistory:" + name, "");

        if (history.contains(newAction)) {
            if (history.contains(";" + newAction)) {
                // Do not duplicate actions in the history; instead, move it to the front
                history = newAction + ";" + history.replace(";" + newAction, "");
                settings.set("FightActionHistory:" + name, history);
            }
            return;
        }

        // Construct a new ;-separated history string
        String[] recentlyUsed = history.split(";");
        String toRemember = action.getIdentifier();
        for (int i = 0; i < (ACTIONS_TO_REMEMBER - 1) && i < recentlyUsed.length; i++) {
            toRemember += ";" + recentlyUsed[i];
        }
        settings.set("FightActionHistory:" + name, toRemember);
    }
}
