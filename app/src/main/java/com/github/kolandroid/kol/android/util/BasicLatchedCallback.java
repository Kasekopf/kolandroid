package com.github.kolandroid.kol.android.util;

public abstract class BasicLatchedCallback<E> implements LatchedCallback<E> {
    private boolean closed = false;

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public final void execute(E item) {
        if (!closed) {
            receiveProgress(item);
        }
    }

    protected abstract void receiveProgress(E message);

    @Override
    public LatchedCallback<E> weak() {
        return new WeakLatchedCallback<>(this);
    }
}
