package com.github.kolandroid.kol.android.screen;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.view.AndroidViewContext;

public class ViewScreen extends FrameLayout implements Screen {
    private Controller controller;
    private View baseView;
    private Screen base;

    public ViewScreen(Context context) {
        super(context);
    }

    public ViewScreen(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewScreen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void display(Controller c, Screen base) {
        this.attach(c, base);
        this.connect();
    }

    public void attach(Controller c, Screen base) {
        this.base = base;
        this.controller = c;

        LayoutInflater inflater = base.getActivity().getLayoutInflater();
        baseView = inflater.inflate(controller.getView(), this, true);
        controller.attach(baseView, this);
    }

    public void connect() {
        if (controller != null) {
            controller.connect(baseView, this);
        }
    }

    public void disconnect() {
        if (controller != null) {
            controller.disconnect(this);
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed
        disconnect();
    }

    @Override
    public FragmentManager getFragmentManager() {
        return base.getFragmentManager();
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
    public FragmentManager getChildFragmentManager() {
        return base.getChildFragmentManager();
    }

    @Override
    public void close() {
        base.close();
    }

}
