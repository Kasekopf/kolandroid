package com.github.kolandroid.kol.model.models.chat.stubs;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.util.Callback;

public class ChatCommandSpawnerStubModel extends ChatStubModel {
    private transient Callback<String> partialChatCallback;

    public ChatCommandSpawnerStubModel(ChatModel base) {
        super(base);
    }

    public void setPartialChatCallback(Callback<String> callback) {
        this.partialChatCallback = callback;
    }

    @Override
    protected void finishChatCommand(Session session, ServerReply reply) {
        this.getGameHandler().handle(session, reply);
    }

    @Override
    public void triggerFill() {
        // do nothing
    }
}
