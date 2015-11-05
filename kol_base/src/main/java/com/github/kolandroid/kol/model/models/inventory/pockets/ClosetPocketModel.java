package com.github.kolandroid.kol.model.models.inventory.pockets;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.Session;

public class ClosetPocketModel extends ItemPocketModel {
    public ClosetPocketModel(String name, Session s, String updateUrl) {
        super(name, s, updateUrl);
    }

    @Override
    protected void loadContent(ServerReply reply) {
        String message;
        if (reply.url.contains("fillcloset.php")) {
            message = "Store";
        } else {
            message = "Take";
        }

        String html = reply.html;
        html = html.replace("[one]", "[" + message + "]");
        html = html.replace("[some]", "[" + message + " some]");
        html = html.replace("[all]", "[" + message + " all]");
        super.loadContent(new ServerReply(reply, html));
    }
}
