package com.starfish.kol.model.models.inventory;

import com.starfish.kol.model.elements.interfaces.DeferredGameAction;
import com.starfish.kol.model.elements.interfaces.Multiuseable;

public interface InventoryActionVisitor {
	public void executeRequest(DeferredGameAction action);
	public void displayAutosell(Multiuseable item);
	public void displayMultiuse(Multiuseable item);
}
