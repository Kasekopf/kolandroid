package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;
import com.github.kolandroid.kol.connection.ConnectionException;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;

public class Request {
    private final String url;

    public Request(String url) {
        this.url = url;
    }

    public void makeAsync(final Session session, final LoadingContext loading, final ResponseHandler handler) {
        Thread t = new Thread() {
            public void run() {
                ServerReply result = makeBlocking(session, loading);
                handler.handle(session, result);
            }
        };
        t.start();
    }

    public ServerReply makeBlocking(Session session, LoadingContext loading) {
        Connection con = getConnection(session.getServer());
        String url = con.getUrl();
        loading.start(url);
        try {
            ServerReply reply = con.complete(session.getCookie());
            loading.complete(url);
            return reply;
        } catch (ConnectionException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            loading.error(url);
            return null;
        }
    }

    protected Connection getConnection(String server) {
        return new Connection("http://" + server + ".kingdomofloathing.com/" + url);
    }


    public String toString() {
        return "$Request[" + url + "]";
    }
}
