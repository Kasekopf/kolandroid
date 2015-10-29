package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;
import com.github.kolandroid.kol.connection.ConnectionException;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.session.Session;

import java.io.FileOutputStream;

/**
 * A request made to external servers, without including any session information.
 */
public class ExternalFileRequest extends ExternalRequest {
    private final FileOutputStream file;

    /**
     * Create a new request for the specified url.
     *
     * @param url The url to request.
     */
    public ExternalFileRequest(String url, FileOutputStream localFile) {
        super(url);
        this.file = localFile;
    }

    @Override
    public ServerReply makeBlocking(Session session, LoadingContext loading) {
        if (loading == null)
            loading = LoadingContext.NONE;

        Connection con = getConnection(session.getServer());
        String url = con.getUrl();
        loading.start(url);
        try {
            con.completeToFile(session.getCookie(), file, loading);
            loading.complete(url);
            return null;
        } catch (ConnectionException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            loading.error(url);
            return null;
        }
    }
}
