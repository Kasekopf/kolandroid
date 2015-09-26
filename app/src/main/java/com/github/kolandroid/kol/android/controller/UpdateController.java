package com.github.kolandroid.kol.android.controller;

public interface UpdateController<E> extends Controller {
    E getModel();

    Class<E> getUpdateType();
}
