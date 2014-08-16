package com.starfish.kol.android.screen;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.starfish.kol.android.controller.Controller;
import com.starfish.kol.gamehandler.ViewContext;

public class ViewScreen extends FrameLayout implements Screen {
    public ViewScreen(Context context) {
        super(context);
    }

    public ViewScreen(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewScreen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    private Screen base;
    
    public void display(Controller c, Screen base) {
    	this.base = base;
    	
    	LayoutInflater inflater = base.getActivity().getLayoutInflater();
    	View view = inflater.inflate(c.getView(), this, true);
    	c.connect(view, this);
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
	public ViewContext getViewContext() {
		return base.getViewContext();
	}

}
