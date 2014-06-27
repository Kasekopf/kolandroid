package com.starfish.kol.model.interfaces;

public interface DeferredAction<E> {
	public void submit(E context);
}
