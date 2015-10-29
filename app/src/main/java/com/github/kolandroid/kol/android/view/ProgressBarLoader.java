package com.github.kolandroid.kol.android.view;

import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.util.ProgressBar;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.util.Callback;

import java.lang.ref.WeakReference;

public class ProgressBarLoader implements LoadingContext {
    private final WeakReference<ProgressBar> base;
    private final WeakReference<Screen> host;

    private final Callback<Boolean> onFinish;
    private boolean closed;

    public ProgressBarLoader(ProgressBar bar, Screen context, Callback<Boolean> onFinish) {
        this.onFinish = onFinish;
        base = new WeakReference<>(bar);
        host = new WeakReference<>(context);
        closed = false;
    }

    public void close() {
        closed = true;
    }

    @Override
    public void start(String page) {
        if (closed) return;
        Screen context = host.get();
        if (context == null) return;

        context.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (closed) return;
                ProgressBar bar = base.get();
                if (bar == null) return;

                bar.setProgress(0, 100);
            }
        });
    }

    @Override
    public void complete(String page) {
        if (closed) return;
        Screen context = host.get();
        if (context == null) return;

        context.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (closed) return;
                ProgressBar bar = base.get();
                if (bar == null) return;

                bar.setProgress(100, 100);
                onFinish.execute(Boolean.TRUE);
            }
        });
    }

    @Override
    public void progress(final int percent) {
        if (closed) return;
        Screen context = host.get();
        if (context == null) return;

        context.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (closed) return;
                ProgressBar bar = base.get();
                if (bar == null) return;

                bar.setProgress(percent, 100);
            }
        });
    }

    @Override
    public void error(String page) {
        if (closed) return;
        Screen context = host.get();
        if (context == null) return;

        context.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (closed) return;
                ProgressBar bar = base.get();
                if (bar == null) return;

                onFinish.execute(Boolean.FALSE);
            }
        });
    }
}
