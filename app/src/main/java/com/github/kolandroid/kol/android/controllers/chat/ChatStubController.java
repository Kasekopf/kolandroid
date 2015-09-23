package com.github.kolandroid.kol.android.controllers.chat;

import android.content.BroadcastReceiver;
import android.view.View;

import com.github.kolandroid.kol.android.chat.ChatBroadcaster;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatStubModel;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

public abstract class ChatStubController<E extends ChatStubModel> extends LinkedModelController<Iterable<ChatModelSegment>, E> {
    private transient BroadcastReceiver updateReceiver;
    private transient HandlerCallback<ChatModel.ChatModelCommand> sendCommand;

    public ChatStubController(E model) {
        super(model);
    }

    @Override
    public final void connect(View view, E model, final Screen host) {
        if (updateReceiver == null) {
            Logger.log("ChatStubController", this.getClass().getSimpleName() + " registering");
            updateReceiver = ChatBroadcaster.registerListener(host, new Callback<Iterable<ChatModelSegment>>() {
                @Override
                public void execute(Iterable<ChatModelSegment> item) {
                    ChatStubModel stub = getModel();
                    synchronized (stub) {
                        stub.apply(item);
                    }
                }
            });

            sendCommand = new HandlerCallback<ChatModel.ChatModelCommand>() {
                @Override
                protected void receiveProgress(ChatModel.ChatModelCommand command) {
                    ChatBroadcaster.sendCommand(host, command);
                }
            };
            model.insertCommandCallback(sendCommand.weak());
        }

        this.doConnect(view, model, host);
    }

    public abstract void doConnect(View view, E model, Screen host);

    @Override
    public void disconnect(Screen host) {
        super.disconnect(host);
        if (updateReceiver != null) {
            Logger.log("ChatStubController", this.getClass().getSimpleName() + " unregistered");
            ChatBroadcaster.unregisterListener(host, updateReceiver);
            updateReceiver = null;
        }

        if (sendCommand != null) {
            sendCommand.close();
            sendCommand = null;
        }
    }
}
