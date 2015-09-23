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
        this.base = base;
        this.controller = c;

        LayoutInflater inflater = base.getActivity().getLayoutInflater();
        View view = inflater.inflate(controller.getView(), this, true);
        controller.attach(view, this);
        controller.connect(view, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed
        if (controller != null)
            controller.disconnect(this);
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
