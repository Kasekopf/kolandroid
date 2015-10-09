package com.github.kolandroid.kol.session.data;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.util.Logger;
import com.google.gson.Gson;

public class FightActionBarData {
    private final int currentPage;
    private final FightActionBarRawElementData[][] pages;

    public FightActionBarData(ServerReply response) {
        Logger.log("FightActionBarData", "Loaded: " + response.html);

        Gson parser = new Gson();

        FightActionBarRawData update = parser.fromJson(response.html, FightActionBarRawData.class);
        this.currentPage = update.whichpage;
        this.pages = update.pages;
    }

    public static FightActionBarData create(ServerReply data) {
        return new FightActionBarData(data);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public FightActionBarRawElementData[][] getPages() {
        return pages;
    }

    public static class FightActionBarRawData {
        public int whichpage;
        public FightActionBarRawElementData[][] pages;
    }

    public static class FightActionBarRawElementData {
        public String type;
        public String id;
        public String pic;
    }
}
