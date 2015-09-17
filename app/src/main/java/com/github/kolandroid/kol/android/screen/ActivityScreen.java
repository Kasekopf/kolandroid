package com.github.kolandroid.kol.android.screen;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.util.Logger;

public abstract class ActivityScreen extends ActionBarActivity implements Screen {
    private ViewContext base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(this.getContentView());
        this.base = this.createViewContext();
        this.setup(savedInstanceState);

        // Display the provided controller
        displayIntent(this.getIntent(), false);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);

        displayIntent(intent, true);
    }

    protected void displayIntent(Intent intent, boolean addToBackStack) {
        if (!intent.hasExtra("controller")) {
            // the intent to launch this came from a back action
            // i.e. back from the chat
            // Do not shift the displayed controller
            Logger.log("ActivityScreen", this.getClass() + " received intent without controller");
            return;
        }

        final Controller controller = (Controller) intent
                .getSerializableExtra("controller");
        displayController(controller, addToBackStack);
    }

    protected ViewContext createViewContext() {
        return new AndroidViewContext(this);
    }

    public abstract void setup(Bundle savedInstanceState);

    protected abstract void displayController(Controller controller, boolean addToBackStack);

    protected abstract int getContentView();

    @Override
    public FragmentManager getChildFragmentManager() {
        return this.getFragmentManager();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public ViewContext getViewContext() {
        return base;
    }

    @Override
    public void close() {
        // do nothing
    }
}
