package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.models.chat.raw.RawAction;
import com.github.kolandroid.kol.request.Request;

public class ChatAction extends Model {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -8557324235833386443L;

    private final RawAction base;

    protected ChatAction(Session s, RawAction base) {
        super(s);

        this.base = base;
    }


    public void submit(ChatText baseMessage, ChatModel context) {
        if (baseMessage.getUser() == null)
            return; //cannot submit with no user

        String player;

        if (base.useId) {
            player = baseMessage.getUser().getId() + "";
        } else {
            player = baseMessage.getUser().getName();
        }

        switch (base.action) {
            case 1:
                Request webReq = new Request(base.entry + "?" + base.arg + "=" + baseMessage.getUser().getId());
                makeRequest(webReq);
                break;
            case 2:
                //noinspection StatementWithEmptyBody
                if (base.submit) {
                    // submit in chat
                    context.submitCommand(new ChatModel.ChatModelCommand.SubmitChatMessage(base.entry + " " + player));
                    break;
                } else {
                    // fallthrough to filling up chat prompt
                }
            case 3: // prompt for text
            case 4: // confirm action
                // in either case, we'll default to filling up the chat prompt
                context.submitCommand(new ChatModel.ChatModelCommand.FillPartialChat(base.entry + " " + player));
                break;
            case 5:
                Request chatReq = new Request(base.entry + baseMessage.getUser().getId());
                makeRequest(chatReq);
        }
    }

    @Override
    public String toString() {
        if (base.title == null) {
            if (base.entry == null) return "[null]";
            return base.entry;
        }
        return base.title;
    }
}