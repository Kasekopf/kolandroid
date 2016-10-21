package com.github.kolandroid.kol.model.models.inventory;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.model.models.inventory.pockets.HagnkPocketModel;
import com.github.kolandroid.kol.model.models.inventory.pockets.HagnkStatusPocketModel;
import com.github.kolandroid.kol.model.models.inventory.pockets.ItemPocket;
import com.github.kolandroid.kol.session.Session;

public class HagnkModel extends ItemStorageModel {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 634354430160L;

    public HagnkModel(Session s, ServerReply text) {
        super(s, text, "storage.php");
    }

    @Override
    protected ItemPocket constructPocket(String name, Session s, String url) {
        if (name.equalsIgnoreCase("hagnk"))
            return new HagnkStatusPocketModel(name, s, url);
        else
            return new HagnkPocketModel(name, s, url);
    }

}