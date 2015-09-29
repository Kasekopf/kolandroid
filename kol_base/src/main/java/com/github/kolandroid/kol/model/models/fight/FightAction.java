package com.github.kolandroid.kol.model.models.fight;

import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.elements.interfaces.SubtextElement;

public interface FightAction extends SubtextElement {
    void attachView(ViewContext context);

    void use();

    String getIdentifier();
}
