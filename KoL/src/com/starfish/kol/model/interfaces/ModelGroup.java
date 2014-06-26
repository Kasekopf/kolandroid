package com.starfish.kol.model.interfaces;

import java.io.Serializable;

public interface ModelGroup<E> extends Serializable {
	public int size();
	public E get(int index);
	public String getName();
}
