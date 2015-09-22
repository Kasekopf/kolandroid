package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;

/**
 * A simulated request, which simply returns the injected ServerReply when executed.
 */
public class SimulatedRequest extends Request {
    // The server reply to respond with
    private final ServerReply toReply;

    /**
     * Create a new simulated response page with the specified URL and Html
     *
     * @param prototype The server reply to use for any other fields
     * @param newUrl    The url to use for the response
     * @param newHtml   The html to use for the response
     */
    public SimulatedRequest(ServerReply prototype, String newUrl, String newHtml) {
        this(new ServerReply(prototype.responseCode, prototype.redirectLocation, prototype.date, newHtml, newUrl, prototype.cookie));
    }

    /**
     * Create a new simulated response with the specified page.
     * @param toReply The page to respond with.
     */
    public SimulatedRequest(ServerReply toReply) {
        super(toReply.url);

        this.toReply = toReply;
    }

    /**
     * Actually make the request and return a response. This should be run on a
     * background thread only!
     *
     * @param session   Session to use to make the request.
     * @param loading   A context (ex. progress bar) to update with any loading information.
     * @return the injected page.
     */
    @Override
    public ServerReply makeBlocking(Session session, LoadingContext loading) {
        loading.start(toReply.url);
        loading.complete(toReply.url);
        return toReply;
    }
}
