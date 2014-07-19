package com.starfish.kol.gamehandler;

import com.starfish.kol.model.Model;

public interface ViewContext {
	public <E extends Model<?>> void display(E model);
}
