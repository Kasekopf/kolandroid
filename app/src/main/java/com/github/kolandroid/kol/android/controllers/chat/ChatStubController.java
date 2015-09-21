package com.github.kolandroid.kol.android.controllers.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

import com.github.kolandroid.kol.android.chat.ChatService;
import com.github.kolandroid.kol.android.chat.ChatServiceBinder;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.ChatStubModel;
import com.github.kolandroid.kol.util.Logger;

public abstract class ChatStubController extends LinkedModelController<Iterable<ChatModelSegment>, ChatStubModel> {
    private transient ServiceConnection service;
    private transient HandlerCallback<ChatModel> callbackWithModel;
    private transient HandlerCallback<Iterable<ChatModelSegment>> callbackWithUpdates;

    public ChatStubController(ChatStubModel model) {
        super(model);
    }

    @Override
    public final void connect(View view, ChatStubModel model, Screen host) {
        this.callbackWithModel = new HandlerCallback<ChatModel>() {
            @Override
            public void receiveProgress(ChatModel baseModel) {
                Logger.log("ChatStubController", "StubModel filled with current state");
                getModel().duplicate(baseModel);
            }
        };
        this.callbackWithUpdates = new HandlerCallback<Iterable<ChatModelSegment>>() {
            @Override
            public void receiveProgress(Iterable<ChatModelSegment> item) {
                getModel().apply(item);
            }
        };

        final HandlerCallback<ChatModel> withModel = this.callbackWithModel;
        final HandlerCallback<Iterable<ChatModelSegment>> withUpdates = this.callbackWithUpdates;
        service = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Logger.log("ChatStubController", "Chat Service bound to " + name);
                ChatServiceBinder binder = (ChatServiceBinder) service;
                binder.acquireModel(withModel.weak(), withUpdates.weak());

                getModel().insertCommandCallback(binder.commandSubmissionCallback());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Logger.log("ChatStubController", "Chat Service unbound from " + name);
            }
        };


        Logger.log("ChatStubController", "Attempting connection with Service");
        Context context = host.getActivity();
        Intent intent = new Intent(context, ChatService.class);
        context.getApplicationContext().bindService(intent, service,
                Context.BIND_AUTO_CREATE);

        this.doConnect(view, model, host);
    }

    public abstract void doConnect(View view, ChatStubModel model, Screen host);

    @Override
    public void disconnect(Screen host) {
        if (service == null) return;

        Context context = host.getActivity();
        context.getApplicationContext().unbindService(service);

        service = null;
        callbackWithModel.close();
        callbackWithModel = null;
        callbackWithUpdates.close();
        callbackWithUpdates = null;
    }
}
