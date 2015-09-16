package com.github.kolandroid.kol.android.screen;

import android.app.Activity;
import android.app.FragmentManager;

import com.github.kolandroid.kol.gamehandler.ViewContext;

public interface Screen {
    FragmentManager getFragmentManager();

    FragmentManager getChildFragmentManager();

    Activity getActivity();

    ViewContext getViewContext();

    void close();
}
