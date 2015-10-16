package com.github.kolandroid.kol.session.data;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.LiveCacheLine;
import com.github.kolandroid.kol.session.cache.SessionCache;
import com.github.kolandroid.kol.util.Callback;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * A wrapper class to parse and store all data related to the KoL api for character status
 */
public class CharacterApiStatusData implements CharacterBasicData, Serializable {
    private CharacterApiStatusData.Raw base;

    private CharacterApiStatusData(CharacterApiStatusData.Raw base) {
        this.base = base;
    }

    @Override
    public String getPwdHash() {
        return base.pwd;
    }

    @Override
    public int getCurrentHP() {
        return base.hp;
    }

    @Override
    public int getCurrentMP() {
        return base.mp;
    }

    @Override
    public int getMaxHP() {
        return base.maxhp;
    }

    @Override
    public int getMaxMP() {
        return base.maxmp;
    }

    @Override
    public int getMeat() {
        return base.meat;
    }

    @Override
    public int getAdventures() {
        return base.adventures;
    }

    @Override
    public int getBaseMuscle() {
        return base.basemuscle;
    }

    @Override
    public int getBuffedMuscle() {
        return base.muscle;
    }

    @Override
    public int getBaseMyst() {
        return base.basemysticality;
    }

    @Override
    public int getBuffedMyst() {
        return base.mysticality;
    }

    @Override
    public int getBaseMoxie() {
        return base.basemoxie;
    }

    @Override
    public int getBuffedMoxie() {
        return base.moxie;
    }

    @Override
    public String getName() {
        return base.name;
    }

    private String getClassName() {
        switch (getClassId()) {
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

    @Override
    public String getAvatar() {
        switch (getClassId()) {
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

    @Override
    public String getTitle() {
        String className = getClassName();
        if (className.isEmpty()) {
            return "Level " + getLevel();
        } else {
            return "Level " + getLevel() + " " + className;
        }
    }

    public int getLevel() {
        return base.level;
    }

    public int getClassId() {
        return base.classId;
    }

    /**
     * Raw JSON data for character status.
     */
    public static class Raw implements Serializable {
        public String pwd;
        public int hp;
        public int mp;
        public int maxhp;
        public int maxmp;
        public int meat;
        public int adventures;

        public String name;
        public int level;

        @SerializedName("class")
        public int classId;

        public int basemuscle;
        public int muscle;

        public int basemysticality;
        public int mysticality;

        public int basemoxie;
        public int moxie;
    }

    /**
     * The cache line for a single CharacterApiStatusData
     */
    public static class Cache extends LiveCacheLine<CharacterApiStatusData> {
        private final Gson parser = new Gson();

        /**
         * Create a new LiveCacheLine in the provided session.
         *
         * @param s The session to use for any requests.
         */
        public Cache(Session s) {
            super(s);
        }

        @Override
        protected CharacterApiStatusData process(ServerReply reply) {
            CharacterApiStatusData.Raw data = parser.fromJson(reply.html, CharacterApiStatusData.Raw.class);
            return new CharacterApiStatusData(data);
        }

        @Override
        protected void computeUrl(SessionCache cache, Callback<String> callback, Callback<Void> failure) {
            callback.execute("api.php?for=koldroid_by_kasekopf&what=status");
        }
    }
}
