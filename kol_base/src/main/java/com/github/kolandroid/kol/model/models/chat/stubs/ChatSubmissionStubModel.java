package com.github.kolandroid.kol.model.models.chat.stubs;

import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.util.Callback;

public class ChatSubmissionStubModel extends ChatStubModel {
    private Callback<String> partialChatCallback;

    public ChatSubmissionStubModel(ChatModel base) {
        super(base);
    }

    public void setPartialChatCallback(Callback<String> callback) {
        this.partialChatCallback = callback;
    }

    @Override
    protected void fillPartialChatPrompt(String message) {
        if (partialChatCallback != null) {
            partialChatCallback.execute(message);
        }
    }
}
