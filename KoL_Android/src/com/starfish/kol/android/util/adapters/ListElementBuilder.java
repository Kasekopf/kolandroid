package com.starfish.kol.android.util.adapters;

import java.io.Serializable;

import android.view.View;

public interface ListElementBuilder<F> extends Serializable{
	public int getChildLayout();
	
	public void fillChild(View view, F child);
}
