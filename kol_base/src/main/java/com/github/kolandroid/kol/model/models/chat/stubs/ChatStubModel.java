package com.github.kolandroid.kol.model.models.chat.stubs;

import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

public class ChatStubModel extends ChatModel {
    private transient Callback<ChatModelCommand> submitCommandCallback;

    public ChatStubModel(Session s) {
        super(s);
    }

    public ChatStubModel(ChatModel base) {
        super(base.getSession());

        this.duplicate(base);
    }

    public void insertCommandCallback(Callback<ChatModelCommand> commandCallback) {
        this.submitCommandCallback = commandCallback;
        triggerFill();
    }

    public void submitCommand(ChatModelCommand command) {
        if (submitCommandCallback == null) {
            Logger.log("ChatStubModel", "Unable to submit command " + command + "; null callback");
        } else {
            Logger.log("ChatStubModel", "Trying to submit command " + command);
            submitCommandCallback.execute(command);
        }
    }

    public void triggerFill() {
        submitCommand(ChatModelCommand.RequestDuplication.ONLY);
    }

    @Override
    protected void log(String message) {
        // ignore logging from other models
    }

    @Override
    protected boolean stubbed() {
        return true;
    }
}
