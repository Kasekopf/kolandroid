package com.github.kolandroid.kol.android.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ChatService extends Service {
    private ChatBroadcaster chat;

    @Override
    public void onCreate() {
        chat = new ChatBroadcaster(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ChatService", "Received start id " + startId + ": " + intent);

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
        return new Binder();
    }
}
