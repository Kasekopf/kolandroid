package com.github.kolandroid.kol.android.chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.ViewContext;

public class ChatService extends Service {
    private ChatServiceBinder chat;

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ChatService", "Received start id " + startId + ": " + intent);

        if (intent != null) {
            chat = new ChatServiceBinder();

            boolean shouldStart = intent.getBooleanExtra("start", false);
            if (shouldStart) {
                Session session = (Session) intent.getSerializableExtra("session");
                ViewContext context = new AndroidViewContext(
                        this.getApplicationContext());
                chat.start(session, context);
            }
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_NOT_STICKY;
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
