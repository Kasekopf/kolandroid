package com.starfish.kol.model.elements.interfaces;

import java.io.Serializable;

import com.starfish.kol.gamehandler.ViewContext;

public interface DeferredGameAction extends Serializable {
	public void submit(ViewContext context);
}
