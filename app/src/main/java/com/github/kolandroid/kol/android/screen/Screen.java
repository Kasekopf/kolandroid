package com.github.kolandroid.kol.android.screen;

import android.app.Activity;
import android.app.FragmentManager;

import com.github.kolandroid.kol.android.view.AndroidViewContext;

public interface Screen {
    FragmentManager getFragmentManager();

    FragmentManager getChildFragmentManager();

    Activity getActivity();

    AndroidViewContext getViewContext();

    void close();
}
