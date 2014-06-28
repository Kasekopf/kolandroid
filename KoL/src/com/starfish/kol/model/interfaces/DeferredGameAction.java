package com.starfish.kol.model.interfaces;

import com.starfish.kol.model.Model;

public interface DeferredGameAction extends DeferredAction<Model<?>> {
	public void submit(Model<?> context);
}
