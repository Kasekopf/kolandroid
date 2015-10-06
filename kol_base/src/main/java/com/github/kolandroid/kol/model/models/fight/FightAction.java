package com.github.kolandroid.kol.model.models.fight;

import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.elements.interfaces.SubtextElement;

public interface FightAction extends SubtextElement {
    FightAction NONE = new FightAction() {
        @Override
        public String getText() {
            return "";
        }

        @Override
        public String getImage() {
            return "http://images.kingdomofloathing.com/itemimages/blank.gif";
        }

        @Override
        public String getSubtext() {
            return "";
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
            return "basic:none";
        }

        @Override
        public boolean matchesActionBarItem(String type, String id) {
            return type == null || id == null;
        }
    };

    void attachView(ViewContext context);

    void use();

    String getIdentifier();

    boolean matchesActionBarItem(String type, String id);
}
