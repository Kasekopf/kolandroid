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
import com.starfish.kol.model.models.chat.ChatModel;
import com.starfish.kol.model.models.chat.ChatModel.ChatStatus;
import com.starfish.kol.util.Callback;

public class ChatManager extends Binder {
	private Timer updateTimer;

	private ChatModel model;
	private boolean started;

	private ArrayList<WeakReference<LatchedCallback<Void>>> listeners;
	
	public ChatManager(Session s, ViewContext context) {
		this.model = new ChatModel(s);
		this.listeners = new ArrayList<WeakReference<LatchedCallback<Void>>>();
		
		this.model.attachView(context);
		this.model.attachCallback(new Callback<ChatStatus>() {
			@Override
			public void execute(ChatStatus message) {
				switch (message) {
				case UPDATE:
					updateListeners();
					break;
				case STOPPED:
					stop();
					break;
				case LOADED:
				case NOCHAT:
					break;
				}

				if (message == ChatStatus.UPDATE)
					updateListeners();

			}
		});
	}

	public void addListener(LatchedCallback<Void> listener) {
		this.listeners.add(new WeakReference<LatchedCallback<Void>>(listener));
		listener.execute(null);
	}

	private void updateListeners() {
		if (!this.started)
			return;

		Iterator<WeakReference<LatchedCallback<Void>>> it = listeners.iterator();
		while (it.hasNext()) {
			LatchedCallback<Void> listener = it.next().get();
			if (listener == null || listener.isClosed()) {
				it.remove();
				continue;
			}
			
			listener.execute(null);
		}
	}

	protected void start() {
		if (this.started) {
			return;
		}
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

	public ChatModel getModel() {
		return model;
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
}
