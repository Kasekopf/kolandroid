package com.github.kolandroid.kol.model;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.LiveModel.LiveMessage;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;

/**
 * A model which can update its contents with a single base url.
 */
public abstract class LiveModel extends LinkedModel<LiveMessage> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 6439988316319232465L;

    private final String updateUrl;
    private final boolean foreground;

    private boolean filling;

    public LiveModel(Session s, String updateUrl, boolean foreground) {
        super(s);

        this.filling = false;
        this.updateUrl = updateUrl;
        this.foreground = foreground;
    }

    public final void process(ServerReply response) {
        this.filling = true;
        this.loadContent(response);
        this.notifyView(LiveMessage.REFRESH);
    }

    protected abstract void loadContent(ServerReply content);

    public void update() {
        Request update = new Request(this.updateUrl);

        ResponseHandler listener = new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                if (response.url.contains(updateUrl)) {
                    process(response);
                } else {
                    if (foreground)
                        getGameHandler().handle(session, response);
                    else
                        System.out.println("LiveModel expected " + updateUrl
                                + " but was redirected to " + response.url);
                }
            }
        };

        if (foreground)
            this.makeRequest(update, listener);
        else
            this.makeRequestBackground(update, listener);
    }

    public void access() {
        if (this.filling)
            return;
        this.filling = true;

        this.update();
    }

    /**
     * A simple enum used for LiveModels to notify their view when they receive new
     * content.
     */
    public enum LiveMessage {
        REFRESH
    }
}