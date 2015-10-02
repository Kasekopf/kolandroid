package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controllers.MessageController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.model.models.MessageModel;
import com.github.kolandroid.kol.model.models.chat.ChannelModel;
import com.github.kolandroid.kol.model.models.chat.ChatAction;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.ChatText;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatStubModel;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;

public class ChatCounterController extends ChatStubController<ChatStubModel> {
    private transient TextView popup;

    private boolean triedInitialStart = false;
    private boolean popupChatIfStarted = false;
    private ChatModelSegment.ChatModelSegmentProcessor chatMonitor;


    public ChatCounterController(Session session) {
        super(new ChatStubModel(session));
    }

    @Override
    public void attach(View view, ChatStubModel model, final Screen host) {
        Logger.log("ChatCounterController", "Updating...");
        popup = (TextView) view.findViewById(R.id.enter_chat_notification);
        if (popup != null) {
            popup.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupChatIfStarted = true;
                if (getModel().hasStarted()) {
                    openChat(host);
                } else {
                    Logger.log("ChatCounterController", "Requesting chat open");
                    getModel().submitCommand(new ChatModel.ChatModelCommand.StartChat(getModel().getSession()));
                }
            }
        });

        chatMonitor = new ChatModelSegment.ChatModelSegmentProcessor() {
            @Override
            public void chatClosed() {
                // Do nothing
            }

            @Override
            public void setLastTime(String time) {
                // Do nothing
            }

            @Override
            public void receiveMessage(ChatText message) {
                // Do nothing
            }

            @Override
            public void setAvailableChannels(ArrayList<String> channels) {
                // Do nothing
            }

            @Override
            public void setCurrentChannels(ArrayList<String> channels) {
                // Do nothing
            }

            @Override
            public void executeCommand(ChatModel.ChatModelCommand command) {
                // Do nothing
            }

            @Override
            public void startChat(String playerId, String pwd, String visibleChannel, ArrayList<ChatAction> baseActions) {
                if (popupChatIfStarted) {
                    popupChatIfStarted = false;
                    openChat(host);
                } else {
                    Logger.log("ChatCounterController", "Chat silently started");
                }
            }

            @Override
            public void startChatFailed(MessageModel message) {
                openChatFailure(host, message);
            }

            @Override
            public void duplicateModel(ChatModel model) {
                SettingsContext settings = host.getViewContext().getSettingsContext();
                boolean shouldStartChat = settings.get("login_enterChat", false);
                if (shouldStartChat && !triedInitialStart && !getModel().hasStarted()) {
                    Logger.log("ChatCounterController", "Automatically opening Chat");
                    getModel().submitCommand(new ChatModel.ChatModelCommand.StartChat(getModel().getSession()));
                    triedInitialStart = true;
                    popupChatIfStarted = false;
                }
            }

            @Override
            public void duplicateChannel(ChannelModel channel) {
                // Do nothing
            }

            @Override
            public void macroResponse(Session session, ServerReply response) {
                // Do nothing
            }
        };
    }

    private void openChat(Screen host) {
        Logger.log("ChatCounterController", "Opening Chat!");
        ChatController controller = new ChatController(getModel());
        host.getViewContext().getPrimaryRoute().execute(controller);

        popup.setVisibility(View.GONE);
    }


    private void openChatFailure(Screen host, MessageModel message) {
        Logger.log("ChatCounterController", "Unable to open Chat: " + message.getMessage());
        MessageController<MessageModel> error = new MessageController<MessageModel>(message);
        host.getViewContext().getPrimaryRoute().execute(error);
    }

    private void checkUnread() {
        int totalUnread = 0;
        for (ChannelModel channel : getModel().getChannels()) {
            totalUnread += channel.getUnreadCount();
        }

        if (popup != null) {
            if (totalUnread == 0) {
                popup.setVisibility(View.GONE);
            } else {
                popup.setVisibility(View.VISIBLE);
                popup.setText("" + totalUnread);
            }
        }
    }

    @Override
    public void receiveProgress(View view, ChatStubModel model, Iterable<ChatModelSegment> message, Screen host) {
        checkUnread();

        for (ChatModelSegment update : message) {
            update.visit(chatMonitor);
        }
    }

    @Override
    public int getView() {
        return R.layout.enter_chat_button_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
