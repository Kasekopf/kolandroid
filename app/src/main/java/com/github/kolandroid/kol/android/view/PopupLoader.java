package com.github.kolandroid.kol.android.view;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kolandroid.kol.gamehandler.LoadingContext;

import java.lang.ref.WeakReference;

public class PopupLoader implements LoadingContext {
    private static final int START = 0;
    private static final int COMPLETE = 1;
    private static final int ERROR = 2;
    private final Handler progressUpdater;

    public PopupLoader(View base, ProgressBar bar, TextView text) {
        this.progressUpdater = new ProgressUpdater(base, bar, text);
    }

    private String shortenURL(String url) {
        return url.replace("https://www.kingdomofloathing.com/", "")
                .replace("http://www.kingdomofloathing.com/", "");
    }

    @Override
    public void start(String page) {
        Message.obtain(progressUpdater, START, shortenURL(page)).sendToTarget();
    }

    @Override
    public void complete(String page) {
        Message.obtain(progressUpdater, COMPLETE, shortenURL(page))
                .sendToTarget();
    }

    @Override
    public void progress(int percent) {
        // do nothing
    }

    @Override
    public void error(String page) {
        Message.obtain(progressUpdater, ERROR, shortenURL(page)).sendToTarget();
    }

    private static class ProgressUpdater extends Handler {
        private final WeakReference<ProgressBar> barRef;
        private final WeakReference<TextView> textRef;
        private final WeakReference<View> baseRef;

        private ProgressUpdater(View base, ProgressBar bar, TextView text) {
            this.baseRef = new WeakReference<>(base);
            this.barRef = new WeakReference<>(bar);
            this.textRef = new WeakReference<>(text);
        }

        public void handleMessage(Message m) {
            ProgressBar bar = barRef.get();
            TextView text = textRef.get();
            View base = baseRef.get();

            if (bar == null || text == null || base == null)
                return;

            switch (m.what) {
                case START:
                    base.setVisibility(View.VISIBLE);
                    text.setText((String) m.obj);
                    base.setBackgroundColor(Color.LTGRAY);
                    break;
                case COMPLETE:
                    text.setText((String) m.obj);
                    AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                    anim.setDuration(1000);
                    anim.setRepeatCount(0);
                    anim.setFillAfter(true);
                    base.startAnimation(anim);
                    break;
                case ERROR:
                    base.setBackgroundColor(Color.RED);
                    AlphaAnimation animError = new AlphaAnimation(1.0f, 0.0f);
                    animError.setDuration(1000);
                    animError.setRepeatCount(0);
                    animError.setFillAfter(true);
                    animError.setStartOffset(1000);
                    base.startAnimation(animError);
                    break;
            }
        }
    }
}
