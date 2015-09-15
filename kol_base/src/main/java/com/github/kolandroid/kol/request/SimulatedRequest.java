package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;

public class SimulatedRequest extends Request {
    private final ServerReply toReply;

    public SimulatedRequest(ServerReply prototype, String newurl, String newhtml) {
        this(new ServerReply(prototype.responseCode, prototype.redirectLocation, prototype.date, newhtml, newurl, prototype.cookie));
    }

    public SimulatedRequest(ServerReply toReply) {
        super(toReply.url);

        this.toReply = toReply;
    }

    /**
     * Actually make the request and return a response. This should be run on a
     * background thread only!
     *
     * @param session Session passed to the response handler.
     */
    @Override
    public ServerReply makeBlocking(Session session, LoadingContext loading) {
        loading.start(toReply.url);
        loading.complete(toReply.url);
        return toReply;
    }
}
