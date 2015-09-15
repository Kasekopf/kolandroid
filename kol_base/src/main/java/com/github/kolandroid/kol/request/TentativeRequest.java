package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;
import com.github.kolandroid.kol.connection.ConnectionException;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;

public class TentativeRequest extends Request {
    private ResponseHandler failure;

    public TentativeRequest(String url, ResponseHandler failure) {
        super(url);

        this.failure = failure;
    }

    @Override
    public void makeAsync(final Session session, final LoadingContext loading, final ResponseHandler handler) {
        Thread t = new Thread() {
            public void run() {
                ServerReply result = makeBlocking(session, loading);
                if (result != null)
                    handler.handle(session, result);
            }
        };
        t.start();
    }

    @Override
    public ServerReply makeBlocking(Session session, LoadingContext loading) {
        Connection con = getConnection(session.getServer());
        String url = con.getUrl();
        loading.start(con.getUrl());
        try {
            ServerReply reply = con.complete(session.getCookie());
            loading.complete(url);
            return reply;
        } catch (ConnectionException e) {
            loading.error(url);
            failure.handle(session, null);
            return null;
        }
    }
}
