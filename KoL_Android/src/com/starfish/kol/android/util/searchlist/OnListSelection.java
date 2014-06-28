package com.starfish.kol.android.util.searchlist;

import java.io.Serializable;

import android.support.v4.app.DialogFragment;

public interface OnListSelection<E> extends Serializable {
	public boolean selectItem(DialogFragment list, E item);
}
