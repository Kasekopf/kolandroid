package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.GroupModel;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;

/**
 * A model for account.php
 */
public class AccountSettingsModel extends GroupModel<LiveWebModel> {
    //Locate the div containing all tabs
    private static final Regex ALL_TABS = new Regex("<div id=[\"']?tabs[\"']?>(.*?)</div>", 1);
    //Locate each tab
    private static final Regex TAB = new Regex("<li.*?</li>", 0);
    //Locate the name of a tab
    private static final Regex TAB_NAME = new Regex("/>(.*?)</a>", 1);
    //Locate the url to update a tab
    private static final Regex TAB_URL = new Regex("<a href=[\"']?([^\"'>]*?)[\"']?>", 1);
    //Locate the margin-left css property; this keeps the tab contents small.
    private static final Regex MARGIN_LEFT = new Regex("margin-left: \\d+px;");

    //An array of models, each representing a different tab of account.php
    private final LiveWebModel[] pages;

    /**
     * Create a new model for account.php
     *
     * @param s     The game session to use for all future requests.
     * @param reply The server reply for account.php to parse.
     */
    public AccountSettingsModel(Session s, ServerReply reply) {
        super(s);

        ArrayList<String> tabs = TAB.extractAllSingle(ALL_TABS.extractSingle(reply.html, ""));
        pages = new LiveWebModel[tabs.size()];
        for (int i = 0; i < tabs.size(); i++) {
            String name = TAB_NAME.extractSingle(tabs.get(i), "[?]");
            String url = TAB_URL.extractSingle(tabs.get(i), "");

            pages[i] = createPageSubModel(s, name, url);
        }
    }

    /**
     * Create a model for an individual tab of the account page.
     * @param s The game session to use for this model.
     * @param title The title of this tab.
     * @param updateUrl The url to use to request the contents of this tab.
     * @return A new LiveWebModel to access this tab.
     */
    private static LiveWebModel createPageSubModel(Session s, String title, String updateUrl) {
        return new LiveWebModel(s, title, updateUrl) {
            @Override
            public String correctHtml(String html) {
                html = ALL_TABS.replaceAll(html, "");   //Remove the left list of settings tabs
                html = MARGIN_LEFT.replaceAll(html, "");//Let the settings fill the pane
                return html;
            }
        };
    }

    /**
     * Get the active child of this model; in this case, the first one.
     * @return  0
     */
    @Override
    public int getActiveChild() {
        return 0;
    }

    /**
     * Get the models for all tabs of the account.php page.
     * @return An array of models, each representing a different tab.
     */
    @Override
    public LiveWebModel[] getChildren() {
        return pages;
    }
}
