package com.github.kolandroid.kol.model.models.chat.stubs;

import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.util.Callback;

public class ChannelStubModel extends ChatStubModel {
    private final String channel;
    private transient Callback<String> partialChatCallback;

    public ChannelStubModel(ChatModel base, String channel) {
        super(base);
        this.channel = channel;
    }

    @Override
    public void triggerFill() {
        submitCommand(new ChatModelCommand.RequestChannelDuplication(channel));
    }
}
