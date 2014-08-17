package com.starfish.kol.android.controller;

import java.io.Serializable;

import android.view.View;

import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.screen.ScreenSelection;

public interface Controller extends Serializable {
	public int getView();
	public void connect(View view, Screen host);
	public void disconnect(Screen host);
	
	public void chooseScreen(ScreenSelection choice);
}
