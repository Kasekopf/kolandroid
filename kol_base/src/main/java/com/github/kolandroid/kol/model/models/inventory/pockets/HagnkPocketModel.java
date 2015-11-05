package com.github.kolandroid.kol.model.models.inventory.pockets;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.Session;

public class HagnkPocketModel extends ItemPocketModel {
    public HagnkPocketModel(String name, Session s, String updateUrl) {
        super(name, s, updateUrl);
    }

    @Override
    protected void loadContent(ServerReply reply) {
        String html = reply.html;
        html = html.replace("[one]", "[take]");
        html = html.replace("[some]", "[take some]");
        html = html.replace("[all]", "[take all]");
        super.loadContent(new ServerReply(reply, html));
    }
}
