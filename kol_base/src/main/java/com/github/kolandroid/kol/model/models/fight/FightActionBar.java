package com.github.kolandroid.kol.model.models.fight;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.model.elements.basic.BasicAction;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.SessionCache;
import com.github.kolandroid.kol.session.cache.CombatActionBarData;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;

public class FightActionBar extends LinkedModel<Void> {
    private static final Regex PWD = new Regex("var pwd=[\"']([a-zA-Z0-9]+)[\"'];", 1);

    private final FightModel parent;
    private int startingPage = 0;
    private ArrayList<ArrayList<BasicAction>> pages = new ArrayList<>();

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public FightActionBar(Session s, ViewContext host, FightModel parent, ServerReply text) {
        super(s);

        this.parent = parent;
        final SettingsContext settings = host.getSettingsContext();
        String pwd = PWD.extractSingle(text.html, "");
        SessionCache cache = host.getDataContext().getSessionCache(s);
        cache.prepare(pwd);
        cache.access(CombatActionBarData.class, new Callback<CombatActionBarData>() {
            @Override
            public void execute(CombatActionBarData item) {
                process(item, settings);
                notifyView(null);
            }
        }, new Callback<Void>() {
            @Override
            public void execute(Void item) {
                // Do nothing
            }
        });
    }

    private void process(CombatActionBarData data, SettingsContext settings) {
        this.startingPage = settings.get("fight_actionbarpage", data.getCurrentPage());
        for (CombatActionBarData.CombatActionBarRawElementData[] page : data.getPages()) {
            ArrayList<BasicAction> newPage = new ArrayList<>();
            for (CombatActionBarData.CombatActionBarRawElementData element : page) {

            }
        }
    }
}
