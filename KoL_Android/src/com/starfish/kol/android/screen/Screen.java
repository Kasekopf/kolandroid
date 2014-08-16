package com.starfish.kol.android.screen;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.starfish.kol.gamehandler.ViewContext;

public interface Screen {
	public FragmentManager getFragmentManager();
	public Activity getActivity();
	public ViewContext getViewContext();
}
