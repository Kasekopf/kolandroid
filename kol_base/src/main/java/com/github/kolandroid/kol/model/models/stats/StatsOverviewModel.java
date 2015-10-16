package com.github.kolandroid.kol.model.models.stats;

import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.request.SimulatedRequest;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.SessionCache;
import com.github.kolandroid.kol.session.data.CharacterBasicData;
import com.github.kolandroid.kol.session.data.CharacterPaneData;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

public class StatsOverviewModel extends LinkedModel<Void> {
    /**
     * Character status data backing this model.
     */
    private CharacterBasicData base;

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
        cache.access(CharacterPaneData.class, new Callback<CharacterPaneData>() {
            @Override
            public void execute(CharacterPaneData item) {
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
        return base.getTitle();
    }

    public String getAvatar() {
        return base.getAvatar();
    }

    /**
     * Display the full character pane in the main window.
     */
    public void displayFull() {
        SessionCache cache = getData().getSessionCache(getSession());
        cache.access(CharacterPaneData.class, new Callback<CharacterPaneData>() {
            @Override
            public void execute(CharacterPaneData item) {
                makeRequest(new SimulatedRequest(item.getPage()));
            }
        }, new Callback<Void>() {
            @Override
            public void execute(Void item) {
                Logger.log("StatsGlanceModel", "Unable to open Character pane");
            }
        });
    }
}
