package com.starfish.kol.android.chat;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.starfish.kol.android.util.AndroidProgressHandler;
import com.starfish.kol.android.view.ApplicationView;
import com.starfish.kol.model.interfaces.DeferredAction;
import com.starfish.kol.model.models.chat.ChatChannel;
import com.starfish.kol.model.models.chat.ChatModel;
import com.starfish.kol.model.models.chat.ChatModel.ChatStatus;
import com.starfish.kol.model.models.chat.ChatText;

public class ChatService extends Service {
	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}

	private ChatCallback callback;

	private ChatModel base;
	private Timer updateTimer = null;

	@Override
	public void onCreate() {

	}

	public void setCallback(ChatCallback back) {
		this.callback = back;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ChatService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		ApplicationView app = (ApplicationView) getApplication();
		this.base = app.getChat();

		this.base.connectView(new AndroidProgressHandler<ChatStatus>() {
			@Override
			public void recieveProgress(ChatStatus message) {
				//Log.i("ChatService", message.toString());
				//Log.i("ChatService", "Now have " + base.getMessages().size()
				//		+ " messages");
				if (message == ChatStatus.UPDATE && callback != null)
					callback.sendUpdate();

				if (updateTimer != null && message == ChatStatus.STOPPED)
					updateTimer.cancel();
			}
		});
		this.base.start();

		if (updateTimer != null)
			updateTimer.cancel();
		updateTimer = new Timer("ChatUpdater", true);
		updateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (base != null)
					base.triggerUpdate();
			}
		}, 1000, 5000);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Tell the user we stopped.
		Toast.makeText(this, "Chat Service Stopped", Toast.LENGTH_SHORT).show();

		if (updateTimer != null)
			updateTimer.cancel();
	}

	public void openChat() {
		if (base.getChatExists()) {
			Intent intent = new Intent(ChatService.this, ChatActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.getApplicationContext().startActivity(intent);
		} else {
			base.displayRejectionMessage();
		}
	}

	public void setCurrentRoom(String room) {
		this.base.setCurrentRoom(room);
	}
	
	public ArrayList<ChatText> getMessages() {
		return base.getMessages();
	}

	public ArrayList<ChatChannel> getChannels() {
		return base.getChannels();
	}

	public void submitChat(String channel, String msg) {
		base.submitChat(channel, msg);
	}
	
	public void submitChatAction(DeferredAction<ChatModel> sub) {
		sub.submit(base);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public static abstract class ChatCallback {
		private ChatService base;
		private AndroidProgressHandler<Void> callback;
		private ServiceConnection service;

		public void open(Activity context) {
			service = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName className,
						IBinder service) {
					LocalBinder binder = (LocalBinder) service;
					base = binder.getService();
					base.setCallback(ChatCallback.this);
					sendUpdate();
				}

				@Override
				public void onServiceDisconnected(ComponentName name) {
					// do nothing
				}
			};

			Intent intent = new Intent(context, ChatService.class);
			context.getApplicationContext().bindService(intent, service,
					Context.BIND_AUTO_CREATE);

			callback = new AndroidProgressHandler<Void>() {
				@Override
				public void recieveProgress(Void message) {
					updateMessages(base);
				}
			};
		}

		private void sendUpdate() {
			callback.reportProgress(null);
		}

		public void close(Activity context) {
			callback.close();
			try {
				context.getApplicationContext().unbindService(service);
			} catch (Exception e) {
				// do nothing
			}
		}
		
		public void refresh() {
			if(base != null)
				base.setCallback(this);
		}

		public ChatService getService() {
			return base;
		}

		public abstract void updateMessages(ChatService base);
	}
}
