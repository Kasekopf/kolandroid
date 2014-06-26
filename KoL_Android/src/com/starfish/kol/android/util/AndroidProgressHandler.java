package com.starfish.kol.android.util;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

import com.starfish.kol.model.ProgressHandler;

public abstract class AndroidProgressHandler<E> implements ProgressHandler<E> {
	private TypedHandler<E> base;
	private boolean closed;

	public AndroidProgressHandler() {
		base = new TypedHandler<E>(this);
		closed = false;
	}

	@Override
	public void reportProgress(E item) {
		if (closed)
			return;
		Message.obtain(base, 0, item).sendToTarget();
	}

	public void close() {
		closed = true;
		base.close();
	}

	public abstract void recieveProgress(E message);

	private static class TypedHandler<E> extends Handler {
		WeakReference<AndroidProgressHandler<E>> parent;
		private boolean closed;

		public TypedHandler(AndroidProgressHandler<E> parent) {
			this.parent = new WeakReference<AndroidProgressHandler<E>>(parent);
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
			AndroidProgressHandler<E> aph = parent.get();
			if (aph == null)
				return;
			aph.recieveProgress(message);
		}
	}
}
