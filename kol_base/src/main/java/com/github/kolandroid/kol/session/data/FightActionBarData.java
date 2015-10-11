package com.github.kolandroid.kol.session.data;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.LiveCacheLine;
import com.github.kolandroid.kol.session.cache.SessionCache;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * A class to parse and store all data related to the KoL "Combat Action Bar"
 */
public class FightActionBarData implements Serializable {
    // Default selected page of the action bar
    private final int currentPage;
    // Data for all 12 action bar pages
    private final FightActionBarRawElementData[][] pages;

    /**
     * Parse the ActionBar from the provided JSON.
     *
     * @param data Parsed JSON object for the action bar
     */
    private FightActionBarData(FightActionBarRawData data) {
        this.currentPage = data.whichpage;
        this.pages = data.pages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public FightActionBarRawElementData[][] getPages() {
        return pages;
    }

    /**
     * Raw JSON data for action bar.
     */
    public static class FightActionBarRawData implements Serializable {
        public int whichpage;
        public FightActionBarRawElementData[][] pages;
    }

    /**
     * Raw JSON data for a single action bar element.
     */
    public static class FightActionBarRawElementData implements Serializable {
        public String type;
        public String id;
        public String pic;
    }

    /**
     * The cache line for a single FightActionBarData
     */
    public static class Cache extends LiveCacheLine<FightActionBarData> {
        private final Gson parser = new Gson();

        /**
         * Create a new FightActionBarData cache in the provided session.
         *
         * @param s The session to use for any requests.
         */
        public Cache(Session s) {
            super(s);
        }

        @Override
        protected FightActionBarData process(ServerReply reply) {
            Logger.log("FightActionBarData", "Loaded: " + reply.html);
            FightActionBarRawData data = parser.fromJson(reply.html, FightActionBarRawData.class);
            return new FightActionBarData(data);
        }

        @Override
        protected void computeUrl(SessionCache cache, final Callback<String> callback, Callback<Void> failure) {
            cache.access(PwdData.class, new Callback<PwdData>() {
                @Override
                public void execute(PwdData item) {
                    callback.execute("actionbar.php?action=fetch&d=" + System.currentTimeMillis() + "&pwd=" + item.getPwd());
                }
            }, failure);
        }

        @Override
        protected Class[] dependencies() {
            return new Class[]{PwdData.class};
        }
    }
}
