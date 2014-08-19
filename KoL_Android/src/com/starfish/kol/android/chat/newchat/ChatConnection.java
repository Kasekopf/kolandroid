package com.starfish.kol.android.chat.newchat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.starfish.kol.android.chat.ChatManager;
import com.starfish.kol.android.chat.ChatService;
import com.starfish.kol.android.util.HandlerCallback;
import com.starfish.kol.model.models.chat.ChatModel;

public abstract class ChatConnection {
	private final ServiceConnection service;
	private HandlerCallback<Void> callback;
	private ChatModel model;

	public ChatConnection(final String connectionName) {
		this.service = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				Log.i("ChatService", "Chat Service bound to " + connectionName);
				ChatManager manager = (ChatManager) service;

				model = manager.getModel();
				onConnection(model);

				callback = new HandlerCallback<Void>() {
					@Override
					protected void recieveProgress(Void arg) {
						recievedRefresh(model);
					}
				};
				manager.addListener(callback);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.i("ChatService", "Chat Service unbound from "
						+ connectionName);
			}
		};
	}

	public void connect(Activity context) {
		Intent intent = new Intent(context, ChatService.class);
		context.getApplicationContext().bindService(intent, service,
				Context.BIND_AUTO_CREATE);
	}

	public void close(Activity context) {
		if (callback != null && this.callback.isClosed())
			return;
		context.getApplicationContext().unbindService(service);
	}

	public boolean isConnected() {
		return (model != null);
	}

	public ChatModel getModel() {
		return model;
	}

	public abstract void onConnection(ChatModel model);

	public abstract void recievedRefresh(ChatModel model);
	
	public static ChatConnection create(String connectionName) {
		return new ChatConnection(connectionName) {
			@Override
			public void onConnection(ChatModel model) {
				// do nothing
			}

			@Override
			public void recievedRefresh(ChatModel model) {
				// do nothing
			}
			
		};
	}
}
