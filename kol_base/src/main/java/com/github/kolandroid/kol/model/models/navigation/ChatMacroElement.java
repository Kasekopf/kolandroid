package com.github.kolandroid.kol.model.models.navigation;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;

import java.util.List;

public class ChatMacroElement extends NavigationElement {
    private static final Regex ARGUMENTS = new Regex("\\$\\d+", 0);

    private final String macro;

    public ChatMacroElement(Session session, String name, String image, String baseUrl, String macro) {
        super(session, name, image, baseUrl);

        this.macro = macro;
    }

    @Override
    public NavigationElement fillArgument(String argument, String value) {
        String newMacro = macro.replace(argument, value);
        return new ChatMacroElement(getSession(), getText(), getImage(), url, newMacro);
    }

    @Override
    public String getSubtext() {
        return macro;
    }

    @Override
    public void submit(Callback<String> forClarification) {
        String argumentRequired = ARGUMENTS.extractSingle(macro, null);
        if (argumentRequired != null) {
            forClarification.execute(argumentRequired);
            return;
        }

        Logger.log("ChatMacroElement", "Triggering " + macro);

        Request r = new Request(url + ChatModel.encodeChatMessage(macro));
        this.makeRequest(r, new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                if (response == null) {
                    Logger.log("NavigationModel", "ChatMacro response: [NULL]");
                    displayMessage("Unable to connect to KoL.");
                    return;
                }

                Logger.log("NavigationModel", "ChatMacro response: " + response.html);
                List<String> messages = ChatModel.chatCommandEval(ChatMacroElement.this, getGameHandler(), response.html);
                for (String message : messages) {
                    displayMessage(message);
                }
            }
        });
    }
}
