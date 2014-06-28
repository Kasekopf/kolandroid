package com.starfish.kol.model.interfaces;


public interface MultiUseableItem extends ModelItem{
	public DeferredGameAction use(String quantity);
}
