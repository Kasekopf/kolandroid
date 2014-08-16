package com.starfish.kol.android.screen;

import com.starfish.kol.android.controller.Controller;

public interface ScreenSelection {
	public void displayExternal(Controller c);
	public void displayPrimary(Controller c);
	public void displayDialog(Controller c);
}
