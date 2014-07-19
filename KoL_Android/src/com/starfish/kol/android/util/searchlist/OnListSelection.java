package com.starfish.kol.android.util.searchlist;

import android.support.v4.app.DialogFragment;

public interface OnListSelection<E> {
	public boolean selectItem(DialogFragment list, E item);
}
