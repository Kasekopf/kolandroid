package com.github.kolandroid.kol.model.models.login;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.model.models.MessageModel;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.request.SimulatedRequest;
import com.github.kolandroid.kol.request.SingleRequest;
import com.github.kolandroid.kol.util.Logger;

public class CreateCharacterModel extends WebModel {
    public CreateCharacterModel(Session s, ServerReply text) {
        super(s, text);

        Logger.log("CreateCharacterModel", "Running");
    }

    @Override
    protected void followUrl(String url) {
        //Override character creation to handle it properly
        if (url.contains("POST/create.php")) {
            Request r = new SingleRequest(url);
            this.makeRequest(r, new ResponseHandler() {
                @Override
                public void handle(Session session, ServerReply response) {
                    if (response == null || response.redirectLocation == null || response.redirectLocation.equals("")) {
                        Logger.log("CreateCharacterModel", "Error creating character; no response from create.php");
                        makeRequest(new SimulatedRequest(MessageModel.generateErrorMessage("Unable to Create Character [0x01]", MessageModel.ErrorType.SEVERE)));
                        return;
                    }

                    session.addCookies(response.cookie);
                    Logger.log("CreateCharacterModel", "Session: " + session);
                    if (session.getCookie("magic", "").equals("")) {
                        Logger.log("CreateCharacterModel", "Error creating character; no magic value set");
                        makeRequest(new SimulatedRequest(MessageModel.generateErrorMessage("Unable to Create Character [0x02]", MessageModel.ErrorType.SEVERE)));
                        return;
                    }

                    SettingsContext settings = getSettings();
                    settings.set("magicSessions", session.getCookie("magic", ""));

                    Request r = new SingleRequest(response.redirectLocation);
                    makeRequest(r, new ResponseHandler() {
                        @Override
                        public void handle(Session session, ServerReply response) {
                            if (response == null || response.redirectLocation == null || response.redirectLocation.equals("")) {
                                Logger.log("CreateCharacterModel", "Error creating character; no response from login.php");
                                makeRequest(new SimulatedRequest(MessageModel.generateErrorMessage("Unable to Create Character [0x03]", MessageModel.ErrorType.SEVERE)));
                                return;
                            }

                            session.addCookies(response.cookie);
                            Logger.log("CreateCharacterModel", "New game session: " + session);
                            if (session.getCookie("PHPSESSID", "").equals("")) {
                                // Failure to login
                                Logger.log("CreateCharacterModel", "Failed to Login");
                                makeRequest(new SimulatedRequest(MessageModel.generateErrorMessage("Unable to Create Character [0x04]", MessageModel.ErrorType.SEVERE)));
                                return;
                            }

                            Request game = new Request("main.php");
                            makeRequest(game);
                        }
                    });
                }
            });
        }
    }
}
