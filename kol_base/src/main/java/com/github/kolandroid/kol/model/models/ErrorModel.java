package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;

public class ErrorModel extends Model {
    private static final Regex TYPE_EXTRACTION = new Regex("[&?]severe=([^&]*)", 1);
    private final String message;
    private final ErrorType severe;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public ErrorModel(Session s, ServerReply reply) {
        super(s);

        this.message = reply.html;
        this.severe = determineType(reply);
    }

    public ErrorModel(String message, ErrorType type) {
        super(new Session());

        this.message = message;
        this.severe = type;
    }

    private static ErrorType determineType(ServerReply text) {
        String specified_type = TYPE_EXTRACTION.extractSingle(text.url, "message");
        for (ErrorType type : ErrorType.values()) {
            if (specified_type.equals(type.toString()))
                return type;
        }
        return ErrorType.MESSAGE;
    }

    public static void trigger(ViewContext context, String message, ErrorType type) {
        Logger.log("ErrorModel", "ERROR: " + message);
        context.getPrimaryRoute().handle(new Session(), generateErrorMessage(message, type));
    }

    public static ServerReply generateErrorMessage(String message, ErrorType type) {
        String url = "androiderror.php&severe=" + type;
        return new ServerReply(200, "", "", message, url, "");
    }

    public String getMessage() {
        return message;
    }

    public <E> E visitType(ErrorTypeVisitor<E> visitor) {
        return severe.visit(visitor);
    }


    public enum ErrorType {
        MESSAGE("message") {
            public <E> E visit(ErrorTypeVisitor<E> visitor) {
                return visitor.forMessage();
            }
        }, ERROR("error") {
            public <E> E visit(ErrorTypeVisitor<E> visitor) {
                return visitor.forError();
            }
        }, SEVERE("results") {
            public <E> E visit(ErrorTypeVisitor<E> visitor) {
                return visitor.forSevere();
            }
        };

        private final String value;

        ErrorType(String value) {
            this.value = value;
        }

        public abstract <E> E visit(ErrorTypeVisitor<E> visitor);

        @Override
        public String toString() {
            return value;
        }
    }

    public interface ErrorTypeVisitor<E> {
        E forMessage();

        E forError();

        E forSevere();
    }
}
