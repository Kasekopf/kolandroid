package com.github.kolandroid.kol.android.util;

import java.lang.ref.WeakReference;

public class WeakLatchedCallback<E> implements LatchedCallback<E> {
    public final WeakReference<LatchedCallback<E>> callbackRef;

    public WeakLatchedCallback(LatchedCallback<E> callback) {
        this.callbackRef = new WeakReference<>(callback);
    }

    @Override
    public boolean isClosed() {
        LatchedCallback<E> callback = callbackRef.get();
        return (callback == null) || callback.isClosed();
    }

    @Override
    public void close() {
        LatchedCallback<E> callback = callbackRef.get();
        if (callback != null) callback.close();
    }

    @Override
    public LatchedCallback<E> weak() {
        return this;
    }

    @Override
    public void execute(E item) {
        LatchedCallback<E> callback = callbackRef.get();
        if (callback != null)
            callback.execute(item);
    }
}
