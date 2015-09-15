package com.github.kolandroid.kol.android.screen;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.github.kolandroid.kol.gamehandler.ViewContext;

public class ActivityScreen implements Screen {
    private final ActionBarActivity base;

    public ActivityScreen(ActionBarActivity base) {
        this.base = base;
    }

    @Override
    public FragmentManager getFragmentManager() {
        return base.getSupportFragmentManager();
    }

    @Override
    public FragmentManager getChildFragmentManager() {
        return base.getSupportFragmentManager();
    }

    @Override
    public Activity getActivity() {
        return base;
    }

    @Override
    public ViewContext getViewContext() {
        return (ViewContext) base;
    }

    @Override
    public void close() {
        // do nothing
    }

}
