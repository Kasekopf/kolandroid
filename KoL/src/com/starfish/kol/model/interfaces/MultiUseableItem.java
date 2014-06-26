package com.starfish.kol.model.interfaces;

import com.starfish.kol.model.Model;

public interface MultiUseableItem extends ModelItem{
	public void use(Model<?> context, String quantity);
}
