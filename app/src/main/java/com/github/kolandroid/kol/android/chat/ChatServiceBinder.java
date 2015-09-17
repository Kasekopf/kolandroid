package com.github.kolandroid.kol.android.chat;

import android.os.Binder;

import com.github.kolandroid.kol.android.util.LatchedCallback;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class ChatServiceBinder extends Binder {
    private final ArrayList<LatchedCallback<Iterable<ChatModelSegment>>> updateListeners;
    private final ArrayList<Callback<ChatModel>> creationListeners;
    private ChatModel model;
    private final LatchedCallback<ChatModelSegment> segmentSubmission = new LatchedCallback<ChatModelSegment>() {
        @Override
        public boolean isClosed() {
            return model != null;
        }

        @Override
        public void execute(ChatModelSegment item) {
            if (model != null) {
                ArrayList<ChatModelSegment> it = new ArrayList<>();
                it.add(item);
                model.apply(it);
            }
        }
    };
    private Timer updateTimer;
    private boolean started;

    public ChatServiceBinder() {
        this.started = false;

        this.updateListeners = new ArrayList<>();
        this.creationListeners = new ArrayList<>();
    }

    private void updateListeners(Iterable<ChatModelSegment> update) {
        if (!this.started)
            return;

        synchronized (updateListeners) {
            Iterator<LatchedCallback<Iterable<ChatModelSegment>>> it = updateListeners.iterator();
            while (it.hasNext()) {
                LatchedCallback<Iterable<ChatModelSegment>> callback = it.next();
                if (callback.isClosed()) {
                    it.remove();
                    Logger.log("ChatServiceBinder", "Listening callback has gone stale");
                } else
                    callback.execute(update);
            }
        }
    }

    protected void start(Session session, final ViewContext context) {
        if (this.started) {
            return;
        }

        ChatModel.start(session, new Callback<ChatModel>() {
            @Override
            public void execute(ChatModel item) {
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
                model.attachCallback(new Callback<Iterable<ChatModelSegment>>() {
                    @Override
                    public void execute(Iterable<ChatModelSegment> item) {
                        updateListeners(item);
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

    public void acquireModel(Callback<ChatModel> withModel, LatchedCallback<Iterable<ChatModelSegment>> withUpdates) {
        Logger.log("ChatServiceBinder", "Registered attempt to connect with ChatModel");
        synchronized (creationListeners) {
            if (model != null) {
                Logger.log("ChatServiceBinder", "Chat already started; returning callback");
                withModel.execute(model);
            } else {
                creationListeners.add(withModel);
            }
        }

        synchronized (updateListeners) {
            updateListeners.add(withUpdates);
        }
    }

    public LatchedCallback<ChatModelSegment> segmentSubmissionCallback() {
        return segmentSubmission;
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
