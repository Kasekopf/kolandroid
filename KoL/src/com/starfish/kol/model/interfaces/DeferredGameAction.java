package com.starfish.kol.model.interfaces;

import com.starfish.kol.model.GameRequestHandler;

public interface DeferredGameAction extends DeferredAction<GameRequestHandler> {
	public void submit(GameRequestHandler context);
}
