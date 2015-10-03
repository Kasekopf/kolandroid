package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.Session;

/**
 * A callback used to process server responses to game requests.
 */
public interface ResponseHandler {
    /**
     * Process a new reply from the server.
     *
     * @param session  The session used when making the request.
     * @param response The response received from the server.
     */
    void handle(Session session, ServerReply response);
}
