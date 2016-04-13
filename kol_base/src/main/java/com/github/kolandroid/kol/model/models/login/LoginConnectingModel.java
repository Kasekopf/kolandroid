package com.github.kolandroid.kol.model.models.login;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.request.ExternalRequest;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

public class LoginConnectingModel extends LinkedModel<ConnectionFailed> {
    /**
     * Create a new model in the provided session.
     */
    public LoginConnectingModel() {
        super(new Session());
    }

    public void doLogin(ViewContext context) {
        // Search for any magic characters! (Magic characters do not have a password set)
        SettingsContext settings = context.getSettingsContext();

        String magicToken = settings.get("magicSessions", "");
        if (!magicToken.equals("")) {
            getSession().addCookies("magic=" + magicToken);
        }

        Logger.log("LoginConnectingModel", "Beginning with session: " + getSession());

        final ResponseHandler mainHandler = context.getPrimaryRoute();
        Request r = new Request("login.php");
        this.makeRequest(r, new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                if (response == null) {
                    Logger.log("LoginConnectingModel", "No internet access");
                    notifyView(ConnectionFailed.NO_ACCESS);
                    return;
                }

                if (!response.url.contains("login.php")) {
                    Logger.log("LoginConnectingModel", "login.php redirected to " + response.url);
                    notifyView(ConnectionFailed.STRANGE_ACCESS);
                    return;
                }

                Logger.log("LoginConnectingModel", "Connected to login.php!");
                mainHandler.handle(session, response);
            }
        });
    }

    /**
     * Check for any app update.
     */
    public void checkAppUpdate(final Callback<AppUpdaterModel> callback) {
        Request appUpdate = new ExternalRequest(AppUpdaterModel.VERSION_URL);
        makeRequest(appUpdate, new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                if (response == null) return;
                callback.execute(new AppUpdaterModel(session, response));
            }
        });
    }
}
