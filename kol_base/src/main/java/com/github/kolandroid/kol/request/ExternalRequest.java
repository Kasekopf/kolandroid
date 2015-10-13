package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.session.Session;

/**
 * A request made to external servers, without including any session information.
 */
public class ExternalRequest extends Request {
    /**
     * Create a new request for the specified url.
     *
     * @param url The url to request.
     */
    public ExternalRequest(String url) {
        super(url);
    }

    @Override
    public ServerReply makeBlocking(Session session, LoadingContext loading) {
        return super.makeBlocking(new Session(), loading); //create and use a new session instead
    }


    protected Connection getConnection(String server) {
        if (this.url.startsWith("http://") || this.url.startsWith("https://")) {
            return new Connection(this.url);
        } else {
            return new Connection("http://" + this.url);
        }
    }
}
