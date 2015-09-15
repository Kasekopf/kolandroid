package com.github.kolandroid.kol.android.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.github.kolandroid.kol.gamehandler.LoadingContext;

import java.lang.ref.WeakReference;

public class ToastLoader implements LoadingContext {
    private final Handler toastLauncher;

    public ToastLoader(Context base) {
        this.toastLauncher = new ToastLauncher(base);
    }

    @Override
    public void start(String page) {
        Message.obtain(toastLauncher, 0, "Loading " + page).sendToTarget();
    }

    @Override
    public void complete(String page) {
        // do nothing
    }

    @Override
    public void error(String page) {
        Message.obtain(toastLauncher, 0, "Loading completed with error").sendToTarget();
    }

    private static class ToastLauncher extends Handler {
        private final WeakReference<Context> base;

        private ToastLauncher(Context context) {
            this.base = new WeakReference<Context>(context);
        }

        public void handleMessage(Message m) {
            Context context = base.get();
            if (context == null)
                return;

            String message = (String) m.obj;
            Toast.makeText(context, message,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
