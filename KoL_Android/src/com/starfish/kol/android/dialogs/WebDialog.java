package com.starfish.kol.android.dialogs;

import android.annotation.SuppressLint;

import com.starfish.kol.android.R;
import com.starfish.kol.android.game.fragments.WebFragment;
import com.starfish.kol.model.models.WebModel;

@SuppressLint("ValidFragment")
public class WebDialog extends WebFragment<WebModel>{
	public WebDialog() {
		super(R.layout.dialog_web_screen);
	}

	protected WebDialog(int layout) {
		super(layout);
	}
}
