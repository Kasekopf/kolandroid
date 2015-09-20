package com.github.kolandroid.kol.android.screen;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.View;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.view.AndroidViewContext;

public class ForcedViewScreen implements Screen {
    private Screen base;

    private View displayOn;

    private Controller controller;

    public ForcedViewScreen(View displayOn) {
        this.displayOn = displayOn;
    }

    public void display(Controller controller, Screen base) {
        this.controller = controller;
        this.base = base;

        controller.connect(displayOn, this);
    }

    public void destroy() {
        if (controller != null) {
            controller.disconnect(this);
        }
    }

    @Override
    public FragmentManager getFragmentManager() {
        return base.getFragmentManager();
    }

    @Override
    public FragmentManager getChildFragmentManager() {
        return base.getChildFragmentManager();
    }

    @Override
    public Activity getActivity() {
        return base.getActivity();
    }

    @Override
    public AndroidViewContext getViewContext() {
        return base.getViewContext();
    }

    @Override
    public void close() {
        base.close();
    }
}
