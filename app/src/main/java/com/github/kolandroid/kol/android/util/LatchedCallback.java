package com.github.kolandroid.kol.android.util;

import com.github.kolandroid.kol.util.Callback;

public interface LatchedCallback<E> extends Callback<E> {
    boolean isClosed();
}
