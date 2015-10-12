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

    private String getClassName() {
        switch (base.getClassId()) {
            case 1:
                return "Seal Clubber";
            case 2:
                return "Turtle Tamer";
            case 3:
                return "Pastamancer";
            case 4:
                return "Sauceror";
            case 5:
                return "Disco Bandit";
            case 6:
                return "Accordion Thief";
            case 11:
                return "Avatar of Boris";
            case 12:
                return "Zombie Master";
            case 14:
                return "Avatar of Jarlsberg";
            case 15:
                return "Avatar of Sneaky Pete";
            case 17:
                return "Ed";
            default:
                //Unknown class
                return "";
        }
    }

    public String getAvatar() {
        switch (base.getClassId()) {
            case 1:
                return "images.kingdomofloathing.com/otherimages/sealclubber.gif";
            case 2:
                return "images.kingdomofloathing.com/otherimages/turtletamer.gif";
            case 3:
                return "images.kingdomofloathing.com/otherimages/pastamancer.gif";
            case 4:
                return "images.kingdomofloathing.com/otherimages/sauceror.gif";
            case 5:
                return "images.kingdomofloathing.com/otherimages/discobandit.gif";
            case 6:
                return "images.kingdomofloathing.com/otherimages/accordionthief.gif";
            case 11:
                return "images.kingdomofloathing.com/otherimages/boris_avatar.gif";
            case 12:
                return "images.kingdomofloathing.com/otherimages/zombavatar.gif";
            case 14:
                return "images.kingdomofloathing.com/otherimages/jarlsberg_avatar.gif";
            case 15:
                return "images.kingdomofloathing.com/otherimages/peteavatar.gif";
            case 17:
                return "images.kingdomofloathing.com/otherimages/ed_av1.gif";
            default:
                //Unknown class
                return "";
        }
    }

    public String getTitle() {
        String className = getClassName();
        if (className.isEmpty()) {
            return "Level " + base.getLevel();
        } else {
            return "Level " + base.getLevel() + " " + className;
        }
    }

    /**
     * Display the full character pane in the main window.
     */
    public void displayFull() {
        //TODO: get it from cache if possible?
        this.makeRequest(new Request("charpane.php"));
    }
}
