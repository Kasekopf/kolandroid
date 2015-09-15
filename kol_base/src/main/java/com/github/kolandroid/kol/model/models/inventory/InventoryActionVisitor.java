package com.github.kolandroid.kol.model.models.inventory;

import com.github.kolandroid.kol.model.elements.interfaces.DeferredGameAction;
import com.github.kolandroid.kol.model.elements.interfaces.Multiuseable;

public interface InventoryActionVisitor {
    void executeRequest(DeferredGameAction action);

    void displayMultiuse(Multiuseable item, String useText);
}
