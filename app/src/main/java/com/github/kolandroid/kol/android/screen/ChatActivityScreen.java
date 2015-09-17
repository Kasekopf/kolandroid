package com.github.kolandroid.kol.android.screen;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.github.kolandroid.kol.gamehandler.ViewContext;

public class ChatActivityScreen implements Screen {
    private final ActionBarActivity base;

    public ChatActivityScreen(ActionBarActivity base) {
        this.base = base;
    }

    @Override
    public FragmentManager getFragmentManager() {
        return base.getFragmentManager();
    }

    @Override
    public FragmentManager getChildFragmentManager() {
        return base.getFragmentManager();
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
