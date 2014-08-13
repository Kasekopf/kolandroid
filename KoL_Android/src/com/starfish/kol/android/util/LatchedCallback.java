package com.starfish.kol.android.util;

import com.starfish.kol.model.ProgressHandler;

public interface LatchedCallback<E> extends ProgressHandler<E> {
	public boolean isClosed();
}
