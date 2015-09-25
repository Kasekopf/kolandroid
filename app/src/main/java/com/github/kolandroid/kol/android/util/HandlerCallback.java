package com.github.kolandroid.kol.android.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class HandlerCallback<E> implements LatchedCallback<E> {
    private final TypedHandler<E> base;
    private boolean closed;

    public HandlerCallback() {
        this.base = new TypedHandler<>(this);
        this.closed = false;
    }

    @Override
    public void execute(E item) {
        if (closed)
            return;

        Message.obtain(base, 0, item).sendToTarget();
    }

    public void execute(E item, int delay) {
        if (closed)
            return;

        base.sendMessageDelayed(Message.obtain(base, 0, item), delay);
    }

    @Override
    public void close() {
        closed = true;
        base.close();
    }

    @Override
    public LatchedCallback<E> weak() {
        return new WeakLatchedCallback<>(this);
    }

    public boolean isClosed() {
        return closed;
    }

    protected abstract void receiveProgress(E message);

    private static class TypedHandler<E> extends Handler {
        final WeakReference<HandlerCallback<E>> parent;
        private boolean closed;

        public TypedHandler(HandlerCallback<E> parent) {
            this.parent = new WeakReference<>(parent);
            this.closed = false;
        }

        public void close() {
            closed = true;
        }

        @Override
        public void handleMessage(Message m) {
            if (closed)
                return;

            @SuppressWarnings("unchecked")
            E message = (E) m.obj;
            HandlerCallback<E> aph = parent.get();
            if (aph == null)
                return;
            aph.receiveProgress(message);
        }
    }
}
