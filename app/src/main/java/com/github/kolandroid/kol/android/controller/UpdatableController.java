package com.github.kolandroid.kol.android.controller;

public interface UpdatableController extends Controller {
    <F> boolean tryApply(Class<F> type, F object);
}
