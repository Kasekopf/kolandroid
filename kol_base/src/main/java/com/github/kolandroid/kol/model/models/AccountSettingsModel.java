package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.GroupModel;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;

public class AccountSettingsModel extends GroupModel<LiveWebModel> {
    private static final Regex ALL_TABS = new Regex("<div id=[\"']?tabs[\"']?>(.*?)</div>", 1);
    private static final Regex TAB = new Regex("<li.*?</li>", 0);
    private static final Regex TAB_NAME = new Regex("/>(.*?)</a>", 1);
    private static final Regex TAB_URL = new Regex("<a href=[\"']?([^\"'>]*?)[\"']?>", 1);
    private static final Regex PWD = new Regex("var pwd = \"(.*?)\";", 1);
    private static final Regex MARGIN_LEFT = new Regex("margin-left: \\d+px;");
    private LiveWebModel[] pages;

    public AccountSettingsModel(Session s, ServerReply reply) {
        super(s);

        String pwd = PWD.extractSingle(reply.html, "");

        ArrayList<String> tabs = TAB.extractAllSingle(ALL_TABS.extractSingle(reply.html, ""));
        pages = new LiveWebModel[tabs.size()];
        for (int i = 0; i < tabs.size(); i++) {
            String name = TAB_NAME.extractSingle(tabs.get(i), "[?]");
            String url = TAB_URL.extractSingle(tabs.get(i), "");

            pages[i] = createPageSubModel(s, name, url);
        }
    }

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

    @Override
    public int getActiveChild() {
        return 0;
    }

    @Override
    public LiveWebModel[] getChildren() {
        return pages;
    }
}
