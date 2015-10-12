package com.github.kolandroid.kol.session.data;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.LiveCacheLine;
import com.github.kolandroid.kol.session.cache.SessionCache;
import com.github.kolandroid.kol.util.Callback;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * A wrapper class to parse and store all data related to the KoL api for character status
 */
public class CharacterStatusData implements Serializable {
    private CharacterStatusData.Raw base;

    private CharacterStatusData(CharacterStatusData.Raw base) {
        this.base = base;
    }

    public String getPwdHash() {
        return base.pwd;
    }

    public int getCurrentHP() {
        return base.hp;
    }

    public int getCurrentMP() {
        return base.mp;
    }

    public int getMaxHP() {
        return base.maxhp;
    }

    public int getMaxMP() {
        return base.maxmp;
    }

    public int getMeat() {
        return base.meat;
    }

    public int getAdventures() {
        return base.adventures;
    }

    public int getBaseMuscle() {
        return base.basemuscle;
    }

    public int getBuffedMuscle() {
        return base.muscle;
    }

    public int getBaseMyst() {
        return base.basemysticality;
    }

    public int getBuffedMyst() {
        return base.mysticality;
    }

    public int getBaseMoxie() {
        return base.basemoxie;
    }

    public int getBuffedMoxie() {
        return base.moxie;
    }

    public String getName() {
        return base.name;
    }

    public int getLevel() {
        return base.level;
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

        public int basemuscle;
        public int muscle;

        public int basemysticality;
        public int mysticality;

        public int basemoxie;
        public int moxie;
    }

    /**
     * The cache line for a single CharacterStatusData
     */
    public static class Cache extends LiveCacheLine<CharacterStatusData> {
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
        protected CharacterStatusData process(ServerReply reply) {
            CharacterStatusData.Raw data = parser.fromJson(reply.html, CharacterStatusData.Raw.class);
            return new CharacterStatusData(data);
        }

        @Override
        protected void computeUrl(SessionCache cache, Callback<String> callback, Callback<Void> failure) {
            callback.execute("api.php?for=koldroid_by_kasekopf&what=status");
        }
    }
}
