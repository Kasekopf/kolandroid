package com.github.kolandroid.kol.session.data;

import com.github.kolandroid.kol.connection.ServerReply;
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
     * Parse the ActionBar from the provided page.
     *
     * @param response Page data to parse the action bar from.
     */
    public FightActionBarData(ServerReply response) {
        Logger.log("FightActionBarData", "Loaded: " + response.html);

        Gson parser = new Gson();

        FightActionBarRawData update = parser.fromJson(response.html, FightActionBarRawData.class);
        this.currentPage = update.whichpage;
        this.pages = update.pages;
    }

    /**
     * Parse the ActionBar from the provided page.
     * @param data  Page data to parse the action bar from.
     * @return Data for the ActionBar contained on the provided page.
     */
    public static FightActionBarData create(ServerReply data) {
        return new FightActionBarData(data);
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
    public static class FightActionBarRawData {
        public int whichpage;
        public FightActionBarRawElementData[][] pages;
    }

    /**
     * Raw JSON data for a single action bar element.
     */
    public static class FightActionBarRawElementData {
        public String type;
        public String id;
        public String pic;
    }
}
