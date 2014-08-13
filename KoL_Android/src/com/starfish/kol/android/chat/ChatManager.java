package com.starfish.kol.android.chat;

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
	
	private ArrayList<LatchedCallback<ChatState>> listeners;
	private DeferredAction<ChatModel> onLoad = null;
	
	
	public ChatManager(Session s, ViewContext context) {
		this.model = new ChatModel(s);
		this.listeners = new ArrayList<LatchedCallback<ChatState>>();
		this.updateTimer = new Timer("ChatUpdater", true);
		
		this.model.connectView(new ProgressHandler<ChatStatus>() {
			@Override
			public void reportProgress(ChatStatus message) {
				switch(message) {
				case UPDATE:
					updateListeners();
					break;
				case STOPPED:
					stop();
					break;
				case LOADED:
				case NOCHAT:
					if(onLoad != null)
						onLoad.submit(model);
					break;
				}
				
				if (message == ChatStatus.UPDATE)
					updateListeners();
				
			}			
		}, context);
	}
	
	public void addListener(LatchedCallback<ChatState> listener) {
		this.listeners.add(listener);
	}

	private void updateListeners() {
		if(!this.started)
			return;
		
		Iterator<LatchedCallback<ChatState>> it = listeners.iterator();
		while(it.hasNext()) {
			LatchedCallback<ChatState> listener = it.next();
			if(listener.isClosed())
				it.remove();
			
			listener.reportProgress(model.getState());
		}
	}

	protected void start() {
		this.start(null);
	}
	
	protected void start(DeferredAction<ChatModel> onLoad) {
		if (this.started)
			return;
		
		this.onLoad = onLoad;
		this.model.start();
		this.started = true;
		this.updateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				model.triggerUpdate();
			}
		}, 1000, 5000);
	}
	
	protected void stop() {
		if(!this.started)
			return;
		this.started = false;
		
		this.updateTimer.cancel();
	}
	
	public void execute(DeferredAction<ChatModel> action) {
		action.submit(model);
	}
}
