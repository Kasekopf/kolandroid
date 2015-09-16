package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.util.Logger;

public class ErrorModel extends Model {
    private final String message;
    private final boolean severe;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public ErrorModel(Session s, ServerReply reply) {
        super(s);

        this.message = reply.html;
        this.severe = reply.url.contains("severe=true");
    }

    public ErrorModel(String message, boolean severe) {
        super(new Session());

        this.message = message;
        this.severe = severe;
    }

    public static void trigger(ViewContext context, String message, boolean severe) {
        Logger.log("ErrorModel", "ERROR: " + message);
        context.getPrimaryRoute().handle(new Session(), generateErrorMessage(message, severe));
    }

    public static ServerReply generateErrorMessage(String message, boolean severe) {
        String url = "androiderror.php&severe=" + severe;
        return new ServerReply(200, "", "", message, url, "");
    }

    public String getMessage() {
        return message;
    }

    public boolean isSevere() {
        return severe;
    }
}
