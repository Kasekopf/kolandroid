package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;
import com.github.kolandroid.kol.connection.ConnectionException;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.session.Session;

/**
 * A request made to the KoL servers to a specified URL, following redirects.
 * Although it can be made in a blocking manner, most uses will use the makeAsync method.
 */
public class Request {
    // The url to access
    protected final String url;

    /**
     * Create a new request for the specified url.
     *
     * @param url The url to request.
     */
    public Request(String url) {
        if (url == null) {
            throw new IllegalArgumentException("Request url cannot be null");
        }
        this.url = url;
    }

    /**
     * Make this request in a new Thread, calling the handler with the result.
     *
     * @param session   Session to use to make the request.
     * @param loading   A context (ex. progress bar) to update with any loading information.
     * @param handler   The handler to call with a result
     */
    public void makeAsync(final Session session, final LoadingContext loading, final ResponseHandler handler) {
        Thread t = new Thread() {
            public void run() {
                ServerReply result = makeBlocking(session, loading);
                handler.handle(session, result);
            }
        };
        t.start();
    }

    /**
     * Actually make the request and return a response. This should be run on a
     * background thread only!
     *
     * @param session   Session to use to make the request.
     * @param loading   A context (ex. progress bar) to update with any loading information.
     * @return the results of the request, or null if the request fails.
     */
    public ServerReply makeBlocking(Session session, LoadingContext loading) {
        if (loading == null)
            loading = LoadingContext.NONE;

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

    /**
     * The connection to use with the request.
     * @param server The server to use for the request.
     * @return a fresh connection to use.
     */
    protected Connection getConnection(String server) {
        if (url.contains("https://www.kingdomofloathing.com/")) {
            return new Connection(url);
        } else if (url.startsWith("/")) {
            return new Connection("https://www.kingdomofloathing.com" + url);
        } else {
            return new Connection("https://www.kingdomofloathing.com/" + url);
        }
    }


    public String toString() {
        return "$Request[" + url + "]";
    }
}
