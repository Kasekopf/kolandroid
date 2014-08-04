package com.starfish.kol.android.view;

import java.lang.ref.WeakReference;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.starfish.kol.gamehandler.LoadingContext;

public class ProgressLoader implements LoadingContext {
	private Handler progressUpdater;

	private static final int START = 0;
	private static final int COMPLETE = 1;
	private static final int ERROR = 2;

	public ProgressLoader(View base, ProgressBar bar, TextView text) {
		this.progressUpdater = new ProgressUpdater(base, bar, text);
	}

	private String shortenURL(String url) {
		return url.replace("http://www.kingdomofloathing.com/", "");
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
	public void error(String page) {
		Message.obtain(progressUpdater, ERROR, shortenURL(page)).sendToTarget();
	}

	private static class ProgressUpdater extends Handler {
		private WeakReference<ProgressBar> barRef;
		private WeakReference<TextView> textRef;
		private WeakReference<View> baseRef;

		private ProgressUpdater(View base, ProgressBar bar, TextView text) {
			this.baseRef = new WeakReference<View>(base);
			this.barRef = new WeakReference<ProgressBar>(bar);
			this.textRef = new WeakReference<TextView>(text);
		}

		public void handleMessage(Message m) {
			ProgressBar bar = barRef.get();
			TextView text = textRef.get();
			View base = baseRef.get();
			
			if (bar == null || text == null || base == null)
				return;

			switch (m.what) {
			case START:
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
				AlphaAnimation animerror = new AlphaAnimation(1.0f, 0.0f);
				animerror.setDuration(1000);
				animerror.setRepeatCount(0);
				animerror.setFillAfter(true);
				animerror.setStartOffset(1000);
				base.startAnimation(animerror);
				break;
			}
		}
	}
}
