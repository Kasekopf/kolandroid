package com.starfish.kol.model.elements.interfaces;

import java.io.Serializable;

public interface DeferredAction<E> extends Serializable {
	public void submit(E context);
}
