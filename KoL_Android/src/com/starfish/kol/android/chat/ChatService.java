package com.starfish.kol.android.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.ViewContext;

public class ChatService extends Service {
	private ChatManager chat;
	
	@Override
	public void onCreate() {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ChatService", "Received start id " + startId + ": " + intent);

		if (intent != null) {
			Session session = (Session) intent.getSerializableExtra("session");
			ViewContext context = new AndroidViewContext(
					this.getApplicationContext());
			chat = new ChatManager(session, context);

			boolean shouldstart = intent.getBooleanExtra("start", false);
			if (shouldstart) {
				chat.start();
			}
		}

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		// Tell the user we stopped.
		Toast.makeText(this, "Chat Service Stopped", Toast.LENGTH_SHORT).show();

		if (chat != null)
			chat.stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return chat;
	}
}
