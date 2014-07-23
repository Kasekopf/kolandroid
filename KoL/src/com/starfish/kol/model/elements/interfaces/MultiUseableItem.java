package com.starfish.kol.model.elements.interfaces;


public interface MultiUseableItem extends ModelElement{
	public DeferredGameAction use(String quantity);
}
