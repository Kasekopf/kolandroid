package com.github.kolandroid.kol.android.controller;

public interface UpdatableController<E> extends Controller {
    void updateModel(E toDisplay);
}
