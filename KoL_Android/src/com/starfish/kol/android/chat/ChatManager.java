package com.starfish.kol.android.chat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Binder;

import com.starfish.kol.android.util.LatchedCallback;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.ProgressHandler;
import com.starfish.kol.model.elements.interfaces.DeferredAction;
import com.starfish.kol.model.models.chat.ChatModel;
import com.starfish.kol.model.models.chat.ChatModel.ChatStatus;
import com.starfish.kol.model.models.chat.ChatState;

public class ChatManager extends Binder {
	private Timer updateTimer;

	private ChatModel model;
	private boolean started;

	private ArrayList<WeakReference<LatchedCallback<ChatState>>> listeners;
	private DeferredAction<ChatModel> onLoad = null;

	public ChatManager(Session s, ViewContext context) {
		this.model = new ChatModel(s);
		this.listeners = new ArrayList<WeakReference<LatchedCallback<ChatState>>>();

		this.model.connectView(new ProgressHandler<ChatStatus>() {
			@Override
			public void reportProgress(ChatStatus message) {
				switch (message) {
				case UPDATE:
					updateListeners();
					break;
				case STOPPED:
					stop();
					break;
				case LOADED:
				case NOCHAT:
					if (onLoad != null)
						onLoad.submit(model);
					break;
				}

				if (message == ChatStatus.UPDATE)
					updateListeners();

			}
		}, context);
	}

	public void addListener(LatchedCallback<ChatState> listener) {
		this.listeners.add(new WeakReference<LatchedCallback<ChatState>>(listener));
		listener.reportProgress(model.getState());
	}

	private void updateListeners() {
		if (!this.started)
			return;

		Iterator<WeakReference<LatchedCallback<ChatState>>> it = listeners.iterator();
		while (it.hasNext()) {
			LatchedCallback<ChatState> listener = it.next().get();
			if (listener == null || listener.isClosed()) {
				it.remove();
				continue;
			}
			
			listener.reportProgress(model.getState());
		}
	}

	protected void start() {
		this.start(null);
	}

	protected void start(DeferredAction<ChatModel> onLoad) {
		if (this.started) {
			if (onLoad != null)
				onLoad.submit(model);
			return;
		}

		this.onLoad = onLoad;
		this.model.start();
		this.started = true;
		if (this.updateTimer != null)
			this.updateTimer.cancel();

		this.updateTimer = new Timer("ChatUpdater", true);
		this.updateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				model.triggerUpdate();
			}
		}, 1000, 5000);
	}

	protected void stop() {
		if (!this.started)
			return;
		this.started = false;

		if (this.updateTimer != null) {
			this.updateTimer.cancel();
			this.updateTimer = null;
		}
	}

	public void execute(DeferredAction<ChatModel> action) {
		action.submit(model);
	}
}
