package com.github.kolandroid.kol.model.models.inventory.pockets;

import com.github.kolandroid.kol.model.models.LiveWebModel;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Regex;

public class HagnkStatusPocketModel extends LiveWebModel implements ItemPocket {
    private static final Regex HEADER_TABS = new Regex("<center>\\[.*?\\]<p><table>");

    public HagnkStatusPocketModel(String name, Session s, String updateUrl) {
        super(s, "Hagnk", updateUrl);
    }

    @Override
    public boolean apply(String itemId, int amountDifference) {
        return false;
    }

    @Override
    public <Result> Result execute(ItemPocketVisitor<Result> visitor) {
        return visitor.display(this);
    }

    @Override
    public String correctHtml(String html) {
        return HEADER_TABS.replaceAll(html, "<table>");
    }
}
