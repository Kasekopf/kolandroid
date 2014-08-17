package com.starfish.kol.android.binders;

import android.view.View;

public interface Binder<E> {
	public int getView();
	public void bind(View view, E model);
}