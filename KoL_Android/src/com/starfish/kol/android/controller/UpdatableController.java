package com.starfish.kol.android.controller;

public interface UpdatableController<E> extends Controller {
	public void updateModel(E toDisplay);
}
