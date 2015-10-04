package com.github.kolandroid.kol.session.cache;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.util.Logger;
import com.google.gson.Gson;

public class CombatActionBarData {
    private final int currentPage;
    private final CombatActionBarRawElementData[][] pages;

    public CombatActionBarData(ServerReply response) {
        Logger.log("CombatActionBarData", "Loaded: " + response.html);

        Gson parser = new Gson();

        CombatActionBarRawData update = parser.fromJson(response.html, CombatActionBarRawData.class);
        this.currentPage = update.whichpage;
        this.pages = update.pages;
    }

    public static CombatActionBarData create(ServerReply data) {
        return new CombatActionBarData(data);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public CombatActionBarRawElementData[][] getPages() {
        return pages;
    }

    public static class CombatActionBarRawData {
        public int whichpage;
        public CombatActionBarRawElementData[][] pages;
    }

    public static class CombatActionBarRawElementData {
        public String type;
        public String id;
        public String pic;
    }
}
