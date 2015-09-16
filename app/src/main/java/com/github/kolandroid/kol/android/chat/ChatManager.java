package com.github.kolandroid.kol.android.chat;

import android.os.Binder;

import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.android.util.LatchedCallback;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModel.ChatStatus;
import com.github.kolandroid.kol.util.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class ChatManager extends Binder {
    private final ArrayList<ChatListener> listeners;
    private ChatModel model;
    private Timer updateTimer;
    private boolean started;

    public ChatManager() {
        this.started = false;
        this.listeners = new ArrayList<ChatListener>();
    }

    public void addListener(LatchedCallback<Void> listener) {
        this.listeners.add(new ChatListener(listener));
        listener.execute(null);
    }

    private void updateListeners() {
        if (!this.started)
            return;

        Iterator<ChatListener> it = listeners.iterator();
        while (it.hasNext()) {
            if (!it.next().update()) {
                it.remove();
            }
        }
    }

    protected void start(Session session, final ViewContext context) {
        if (this.started) {
            return;
        }

        ChatModel.start(session, new HandlerCallback<ChatModel>() {
            @Override
            public void receiveProgress(ChatModel item) {
                started = true;
                model = item;

                Logger.log("ChatManager", "Chat connected to [" + ChatManager.this + "]");
                model.attachView(context);
                model.attachCallback(new HandlerCallback<ChatStatus>() {
                    @Override
                    public void receiveProgress(ChatStatus message) {
                        switch (message) {
                            case UPDATE:
                                updateListeners();
                                break;
                            case STOPPED:
                                stop();
                                break;
                        }
                    }
                });

                if (updateTimer != null)
                    updateTimer.cancel();
                updateTimer = new Timer("ChatUpdater", true);
                updateTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        model.triggerUpdate();
                    }
                }, 1000, 5000);
            }
        });
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

    private static class ChatListener {
        private final WeakReference<LatchedCallback<Void>> callback;

        public ChatListener(LatchedCallback<Void> callback) {
            this.callback = new WeakReference<LatchedCallback<Void>>(callback);
        }

        public boolean update() {
            LatchedCallback<Void> listener = callback.get();
            if (listener == null || listener.isClosed()) {
                return false;
            }

            listener.execute(null);
            return true;
        }
    }
}
