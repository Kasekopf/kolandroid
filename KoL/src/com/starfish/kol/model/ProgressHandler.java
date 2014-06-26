package com.starfish.kol.model;

public interface ProgressHandler<E> {
	public void reportProgress(E item);
}
