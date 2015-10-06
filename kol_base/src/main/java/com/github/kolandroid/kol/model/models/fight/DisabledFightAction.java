package com.github.kolandroid.kol.model.models.fight;

import com.github.kolandroid.kol.gamehandler.ViewContext;

public class DisabledFightAction implements FightAction {
    private final String type;
    private final String id;
    private final String image;

    public DisabledFightAction(String type, String id, String image) {
        this.type = type;
        this.id = id;

        if (image == null || image.isEmpty()) {
            this.image = "http://images.kingdomofloathing.com/itemimages/blank.gif";
        } else {
            this.image = "http://images.kingdomofloathing.com/itemimages/" + image;
        }
    }

    @Override
    public void attachView(ViewContext context) {
        // Do nothing
    }

    @Override
    public void use() {
        // Do nothing
    }

    @Override
    public String getIdentifier() {
        return type + ":" + id;
    }

    @Override
    public boolean matchesActionBarItem(String type, String id) {
        return this.type.equals(type) && this.id.equals(id);
    }

    @Override
    public String getSubtext() {
        return "";
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String getImage() {
        return image;
    }
}
