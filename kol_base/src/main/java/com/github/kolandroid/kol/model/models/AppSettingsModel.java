package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.models.login.AppUpdaterModel;
import com.github.kolandroid.kol.request.ExternalRequest;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Callback;

public class AppSettingsModel extends Model {
    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public AppSettingsModel(Session s) {
        super(s);
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
