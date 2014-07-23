package com.starfish.kol.model.elements.interfaces;

import com.starfish.kol.gamehandler.ViewContext;

public interface DeferredGameAction extends DeferredAction<ViewContext> {
	public void submit(ViewContext context);
}
