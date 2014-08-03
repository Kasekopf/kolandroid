package com.starfish.kol.android.view;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.starfish.kol.gamehandler.LoadingContext;

public class ToastLoader implements LoadingContext {
	private Handler toastLauncher;

	public ToastLoader(Context base) {
		this.toastLauncher = new ToastLauncher(base);
	}

	@Override
	public void reportProgress(String page, int current, int total) {
		Message.obtain(toastLauncher, 0, "Loading " + page).sendToTarget();
	}

	@Override
	public void complete(String page, boolean error) {
		if (error) {
			Message.obtain(toastLauncher, 0, "Loading completed with error").sendToTarget();
		}
	}

	private static class ToastLauncher extends Handler {
		private WeakReference<Context> base;

		private ToastLauncher(Context context) {
			this.base = new WeakReference<Context>(context);
		}

		public void handleMessage(Message m) {
			Context context = base.get();
			if(context == null)
				return;
			
			String message = (String)m.obj;
			Toast.makeText(context, message,
					Toast.LENGTH_SHORT).show();
		}
	}
}
