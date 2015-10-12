package com.github.kolandroid.kol.model.models.stats;

import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.SessionCache;
import com.github.kolandroid.kol.session.data.CharacterStatusData;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

public class StatsOverviewModel extends LinkedModel<Void> {
    /**
     * Character status data backing this model.
     */
    private CharacterStatusData base;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public StatsOverviewModel(Session s) {
        super(s);
    }

    /**
     * Force an update of the character status.
     */
    public void update() {
        SessionCache cache = getData().getSessionCache(getSession());
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

    private String format(int baseStat, int buffedStat) {
        if (baseStat == buffedStat) {
            return baseStat + "";
        } else {
            return "<font color=blue>" + buffedStat + "</font> (" + baseStat + ")";
        }
    }

    public String getMuscle() {
        return format(base.getBaseMuscle(), base.getBuffedMuscle());
    }

    public String getMyst() {
        return format(base.getBaseMyst(), base.getBuffedMyst());
    }

    public String getMoxie() {
        return format(base.getBaseMoxie(), base.getBuffedMoxie());
    }

    public String getName() {
        return base.getName();
    }

    public String getTitle() {
        return "" + base.getLevel();
    }

    /**
     * Display the full character pane in the main window.
     */
    public void displayFull() {
        //TODO: get it from cache if possible?
        this.makeRequest(new Request("charpane.php"));
    }
}
