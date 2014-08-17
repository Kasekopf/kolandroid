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
import com.starfish.kol.android.util.AndroidProgressHandler;
import com.starfish.kol.model.models.chat.ChatModel;
import com.starfish.kol.model.models.chat.ChatState;


public abstract class LiveChatConnection {
	private final ServiceConnection service;
	private AndroidProgressHandler<ChatState> callback;
	private boolean open;
	
	public LiveChatConnection(final String connectionName) {
		this.service = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				Log.i("ChatService", "Chat Service bound to " + connectionName);
				ChatManager manager = (ChatManager) service;
				if(open)
					onConnection(manager.getModel());
				
				callback = new AndroidProgressHandler<ChatState>() {
					@Override
					protected void recieveProgress(ChatState message) {
						recievedRefresh();
					}
				};
				manager.addListener(callback);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.i("ChatService", "Chat Service unbound from " + connectionName);
			}
		};
	}
	
	public void connect(Activity context) {
		if(callback == null || callback.isClosed()) {
			callback = new AndroidProgressHandler<ChatState>() {
				@Override
				public void recieveProgress(ChatState messages) {
					if(open)
						recievedRefresh();
				}
			};
			open = true;

			Intent intent = new Intent(context, ChatService.class);
			context.getApplicationContext().bindService(intent, service,
					Context.BIND_AUTO_CREATE);
		}
	}
	
	public void close(Activity context) {
		open = false;
		context.getApplicationContext().unbindService(service);
	}
	
	public abstract void onConnection(ChatModel model);
	public abstract void recievedRefresh();
}
