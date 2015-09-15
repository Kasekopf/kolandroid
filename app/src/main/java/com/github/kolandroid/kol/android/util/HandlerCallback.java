package com.github.kolandroid.kol.android.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class HandlerCallback<E> implements LatchedCallback<E> {
    private TypedHandler<E> base;
    private boolean closed;

    public HandlerCallback() {
        base = new TypedHandler<E>(this);
        closed = false;
    }

    @Override
    public void execute(E item) {
        if (closed)
            return;
        Message.obtain(base, 0, item).sendToTarget();
    }

    public void close() {
        closed = true;
        base.close();
    }

    public boolean isClosed() {
        return closed;
    }

    protected abstract void receiveProgress(E message);

    private static class TypedHandler<E> extends Handler {
        WeakReference<HandlerCallback<E>> parent;
        private boolean closed;

        public TypedHandler(HandlerCallback<E> parent) {
            this.parent = new WeakReference<HandlerCallback<E>>(parent);
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
