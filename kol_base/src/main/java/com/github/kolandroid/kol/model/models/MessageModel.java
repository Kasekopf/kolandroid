package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Regex;

public class MessageModel extends Model {
    private static final Regex TYPE_EXTRACTION = new Regex("[&?]severe=([^&]*)", 1);

    private final String message;
    private final String actionText;
    private final String action;
    private final String title;
    private final ErrorType error;

    /**
     * Create a new model in the provided session.
     *
     * @param s          Session to use in all future requests by this model.
     * @param title      Title of the message
     * @param message    The message to display
     * @param actionText Text to use to identify the action.
     * @param action     Url to trigger when the action is done.
     * @param error      The type of error message
     */
    public MessageModel(Session s, String title, String message, String actionText, String action, ErrorType error) {
        super(s);
        this.message = message;
        this.actionText = actionText;
        this.action = action;
        this.title = title;
        this.error = error;
    }

    public MessageModel(String message, ErrorType error) {
        super(new Session());
        this.message = message;
        this.actionText = "";
        this.action = "";
        this.error = error;
        this.title = error.getDefaultTitle();
    }

    public MessageModel(Session s, ServerReply reply) {
        this(reply.html, determineType(reply));
    }

    public static ServerReply generateErrorMessage(String message, ErrorType type) {
        String url = "androiderror.php&severe=" + type;
        return new ServerReply(200, "", "", message, url, "");
    }

    private static ErrorType determineType(ServerReply text) {
        String specified_type = TYPE_EXTRACTION.extractSingle(text.url, "message");
        for (ErrorType type : ErrorType.values()) {
            if (specified_type.equals(type.toString()))
                return type;
        }
        return ErrorType.NONE;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void triggerAction() {
        if (hasAction()) {
            this.makeRequest(new Request(action));
        }
    }

    public <E> E visitErrorType(ErrorTypeVisitor<E> visitor) {
        return this.error.visit(visitor);
    }

    public boolean hasAction() {
        return !action.equals("");
    }

    public String getActionText() {
        return actionText;
    }


    public enum ErrorType {
        NONE("none", "Message:") {
            public <E> E visit(ErrorTypeVisitor<E> visitor) {
                return visitor.forNone();
            }
        }, ERROR("error", "Error:") {
            public <E> E visit(ErrorTypeVisitor<E> visitor) {
                return visitor.forError();
            }
        }, SEVERE("severe", "Error:") {
            public <E> E visit(ErrorTypeVisitor<E> visitor) {
                return visitor.forSevere();
            }
        };

        private final String value;
        private final String defaultTitle;

        ErrorType(String value, String defaultTitle) {
            this.value = value;
            this.defaultTitle = defaultTitle;
        }

        public abstract <E> E visit(ErrorTypeVisitor<E> visitor);

        protected String getDefaultTitle() {
            return defaultTitle;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @SuppressWarnings("SameReturnValue")
    public interface ErrorTypeVisitor<E> {
        E forNone();

        E forError();

        E forSevere();
    }
}
