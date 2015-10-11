package com.github.kolandroid.kol.model.models.stats;

import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.SessionCache;
import com.github.kolandroid.kol.session.data.CharacterStatusData;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

public class StatsGlanceModel extends LinkedModel<Void> {
    /**
     * Character status data backing this model.
     */
    private CharacterStatusData base;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public StatsGlanceModel(Session s) {
        super(s);
    }

    public int getCurrentHP() {
        return base.getCurrentHP();
    }

    public int getCurrentMP() {
        return base.getCurrentMP();
    }

    public int getMaxHP() {
        return base.getMaxHP();
    }

    public int getMaxMP() {
        return base.getMaxMP();
    }

    public int getMeat() {
        return base.getMeat();
    }

    public int getAdventures() {
        return base.getAdventures();
    }

    /**
     * Force an update of the character status.
     */
    public void update() {
        SessionCache cache = getData().getSessionCache(getSession());
        cache.clear(CharacterStatusData.class);
        cache.access(CharacterStatusData.class, new Callback<CharacterStatusData>() {
            @Override
            public void execute(CharacterStatusData item) {
                base = item;
                notifyView(null);
            }
        }, new Callback<Void>() {
            @Override
            public void execute(Void item) {
                Logger.log("StatsGlanceModel", "Unable to load new character status");
            }
        });
    }

    /**
     * Display the full character pane in the main window.
     */
    public void displayFull() {
        //TODO: get it from cache if possible?
        this.makeRequest(new Request("charpane.php"));
    }
}
