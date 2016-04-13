package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Callback;

public class ChatLink extends ChatAction {
    private final String url;

    protected ChatLink(Session s, String url) {
        super(s, "Go to " + (url.contains("http://") ? url : "https://www.kingdomofloathing.com/" + url));

        this.url = ((url.contains("http://") || url.contains("https://")) ? url : "https://www.kingdomofloathing.com/" + url);
    }

    @Override
    public void submit(ChatText baseMessage, ChatModel context, Callback<String> submitExternalUrl) {
        if (url.contains("https://www.kingdomofloathing.com") || url.contains("http://www.kingdomofloathing.com")) {
            this.makeRequest(new Request(url));
        } else {
            submitExternalUrl.execute(url);
        }
    }
}
