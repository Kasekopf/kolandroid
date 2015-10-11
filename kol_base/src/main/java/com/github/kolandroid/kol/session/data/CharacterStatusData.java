package com.github.kolandroid.kol.session.data;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.LiveCacheLine;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.SessionCache;
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

    /**
     * Get the password hash of the current session.
     *
     * @return The password hash of the current session.
     */
    public String getPwdHash() {
        return base.pwd;
    }

    /**
     * Raw JSON data for character status.
     */
    public static class Raw {
        public String pwd;
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
