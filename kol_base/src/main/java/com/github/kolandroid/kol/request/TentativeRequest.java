package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;
import com.github.kolandroid.kol.connection.ConnectionException;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;

/**
 * A request which consults a separate handler if the request fails.
 */
public class TentativeRequest extends Request {
    // The handler to consult for a failed request
    private final ResponseHandler failure;

    /**
     * Create a new request for the specified url, with a handler for if a failure occurs.
     *
     * @param url     The url to request.
     * @param failure A handler to use if the request fails.
     */
    public TentativeRequest(String url, ResponseHandler failure) {
        super(url);

        this.failure = failure;
    }

    /**
     * Make this request in a new Thread, calling the handler with the result if the request succeeds.
     *
     * @param session Session to use to make the request.
     * @param loading A context (ex. progress bar) to update with any loading information.
     * @param handler The handler to call with a result
     */
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

    /**
     * Actually make the request and return a response. This should be run on a
     * background thread only!
     *
     * @param session   Session to use to make the request.
     * @param loading   A context (ex. progress bar) to update with any loading information.
     * @return the results of the request, or null if the request fails.
     */
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
