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

	private Controller controller;
	private Screen base;

	public void display(Controller c, Screen base) {
		this.base = base;
		this.controller = c;

		LayoutInflater inflater = base.getActivity().getLayoutInflater();
		View view = inflater.inflate(controller.getView(), this, true);
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
	public ViewContext getViewContext() {
		return base.getViewContext();
	}

	@Override
	public FragmentManager getChildFragmentManager() {
		return base.getChildFragmentManager();
	}

	@Override
	public void close() {
		// do nothing
	}

}
