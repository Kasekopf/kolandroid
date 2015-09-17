package com.github.kolandroid.kol.android.chat.service;

import android.os.Binder;

import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.android.util.LatchedCallback;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.models.chat.chatold.ChatModel;
import com.github.kolandroid.kol.model.models.chat.chatold.ChatModelSegment;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class ChatServiceBinder extends Binder {
    private final ArrayList<LatchedCallback<Void>> listeners;
    private final ArrayList<Callback<ChatModel>> creationListeners;

    private ChatModel model;
    private Timer updateTimer;
    private boolean started;

    public ChatServiceBinder() {
        this.started = false;

        this.listeners = new ArrayList<>();
        this.creationListeners = new ArrayList<>();
    }

    public void addListener(LatchedCallback<Void> listener) {
        this.listeners.add(listener);
        listener.execute(null);
    }

    private void updateListeners() {
        if (!this.started)
            return;

        Iterator<LatchedCallback<Void>> it = listeners.iterator();
        while (it.hasNext()) {
            LatchedCallback<Void> callback = it.next();
            if (callback.isClosed())
                it.remove();
            else
                callback.execute(null);
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

                synchronized (creationListeners) {
                    model = item;
                    for (Callback<ChatModel> creationListener : creationListeners) {
                        creationListener.execute(model);
                    }
                    creationListeners.clear();
                }

                Logger.log("ChatServiceBinder", "Chat connected to [" + ChatServiceBinder.this + "]");
                model.attachView(context);
                model.attachCallback(new HandlerCallback<Iterable<ChatModelSegment>>() {
                    @Override
                    protected void receiveProgress(Iterable<ChatModelSegment> message) {
                        updateListeners();
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

    public void acquireModel(Callback<ChatModel> callback) {
        synchronized (creationListeners) {
            if (model != null) {
                callback.execute(model);
            } else {
                creationListeners.add(callback);
            }
        }
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
