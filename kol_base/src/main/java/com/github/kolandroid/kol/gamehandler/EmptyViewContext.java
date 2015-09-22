package com.github.kolandroid.kol.gamehandler;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.data.RawSkill;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Logger;

import java.io.Serializable;

public class EmptyViewContext implements ViewContext, Serializable {
    public static final EmptyViewContext ONLY = new EmptyViewContext();

    private EmptyViewContext() {
    }

    @Override
    public ResponseHandler getPrimaryRoute() {
        return new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                if (response == null) {
                    Logger.log("EmptyViewContext", "ERROR: Attempted to access server");
                } else {
                    Logger.log("EmptyViewContext", "ERROR: Attempted to access url " + response.url);
                }
            }
        };
    }

    @Override
    public LoadingContext createLoadingContext() {
        return LoadingContext.NONE;
    }

    @Override
    public DataContext getDataContext() {
        return new DataContext() {
            @Override
            public DataCache<String, RawSkill> getSkillCache() {
                return new EmptyDataCache<>();
            }

            @Override
            public DataCache<String, RawItem> getItemCache() {
                return new EmptyDataCache<>();
            }
        };
    }

    @Override
    public void displayMessage(String message) {
        Logger.log("EmptyViewContext", "ERROR: Attempted to display message " + message);
    }

    @Override
    public SettingsContext getSettingsContext() {
        Logger.log("EmptyViewContext", "ERROR: Attempted to load settings");
        return SettingsContext.NONE;
    }
}
