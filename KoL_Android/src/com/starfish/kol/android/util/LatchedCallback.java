package com.starfish.kol.android.util;

import com.starfish.kol.util.Callback;

public interface LatchedCallback<E> extends Callback<E> {
	public boolean isClosed();
}
