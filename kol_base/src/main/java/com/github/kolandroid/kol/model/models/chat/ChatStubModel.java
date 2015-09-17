package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

public class ChatStubModel extends ChatModel {
    private transient Callback<ChatModelSegment> submitSegmentCallback;

    public ChatStubModel(Session s) {
        super(s);
    }

    public ChatStubModel(ChatModel base) {
        super(base.getSession());

        this.duplicate(base);
    }

    private void submitSegment(ChatModelSegment segment) {
        if (submitSegmentCallback == null) {
            Logger.log("ChatStubModel", "Unable to submit segment " + segment + "; null callback");
            return;
        }

        submitSegmentCallback.execute(segment);
    }

    public void insertSegmentCallback(Callback<ChatModelSegment> callback) {
        this.submitSegmentCallback = callback;
    }

    @Override
    protected void submitChat(String msg) {
        ChatModelSegment action = new ChatModelSegment.SubmitChatMessage(msg);
        submitSegment(action);
    }

    @Override
    protected void leaveChannel(String channel) {
        ChatModelSegment action = new ChatModelSegment.LeaveChannel(channel);
        submitSegment(action);
    }

    @Override
    protected void log(String message) {
        // ignore logging from other models
    }

    @Override
    public void triggerUpdate() {
        // do not independently get updates
    }

    @Override
    protected void submitChat(String msg, boolean hiddenCommand) {
        // do not actually send chat messages
    }

    @Override
    protected boolean stubbed() {
        return true;
    }
}
