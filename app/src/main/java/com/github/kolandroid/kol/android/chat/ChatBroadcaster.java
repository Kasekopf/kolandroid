package com.github.kolandroid.kol.android.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.github.kolandroid.kol.android.game.GameScreen;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.models.MessageModel;
import com.github.kolandroid.kol.model.models.chat.ChatAction;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.ChatText;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChatBroadcaster {
    private static final String CHAT_COMMAND_INTENT_TAG = "CHAT_COMMAND";
    private static final String CHAT_SEGMENT_INTENT_TAG = "CHAT_SEGMENT";

    private static volatile int listenerCount = 0; //not quite thread safe, but it's only for logging
    private final LocalBroadcastManager broadcastManager;
    private final ViewContext context;

    private volatile ChatModel chat;
    private volatile Timer updateTimer;

    private final ChatModelSegment.ChatModelSegmentProcessor chatMonitor = new ChatModelSegment.ChatModelSegmentProcessor() {
        @Override
        public void chatClosed() {
            stop();
        }

        @Override
        public void setLastTime(String time) {

        }

        @Override
        public void receiveMessage(ChatText message) {

        }

        @Override
        public void setAvailableChannels(ArrayList<String> channels) {

        }

        @Override
        public void setCurrentChannels(ArrayList<String> channels) {

        }

        @Override
        public void executeCommand(ChatModel.ChatModelCommand command) {

        }

        @Override
        public void startChat(String playerId, String pwd, String visibleChannel, ArrayList<ChatAction> baseActions) {
            if (updateTimer != null)
                updateTimer.cancel();
            updateTimer = new Timer("ChatUpdater", true);
            updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    chat.submitCommand(ChatModel.ChatModelCommand.UpdateChat.ONLY);
                }
            }, 1000, 5000);
        }

        @Override
        public void startChatFailed(MessageModel message) {
            stop();
        }

        @Override
        public void duplicateModel(ChatModel model) {

        }
    };

    public ChatBroadcaster(Context context) {
        Logger.log("ChatBroadcaster", "Created");

        BroadcastReceiver commandReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("command")) {
                    ChatModel.ChatModelCommand command = (ChatModel.ChatModelCommand) intent.getSerializableExtra("command");
                    Logger.log("ChatBroadcaster", "Received command " + command);
                    if (command instanceof ChatModel.ChatModelCommand.StartChat) {
                        Session session = ((ChatModel.ChatModelCommand.StartChat) command).getSession();
                        createNewChat(session);
                    }

                    if (chat != null) {
                        synchronized (chat) {
                            chat.submitCommand(command);
                        }
                    }
                }
            }
        };

        broadcastManager = LocalBroadcastManager.getInstance(context);
        broadcastManager.registerReceiver(commandReceiver, new IntentFilter(CHAT_COMMAND_INTENT_TAG));

        this.context = new AndroidViewContext(context, GameScreen.class);
    }

    public static void sendCommand(Screen host, ChatModel.ChatModelCommand command) {
        Logger.log("ChatBroadcaster", "Sent command " + command);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(host.getActivity());
        Intent intent = new Intent(CHAT_COMMAND_INTENT_TAG);
        intent.putExtra("command", command);
        broadcastManager.sendBroadcast(intent);
    }

    public static BroadcastReceiver registerListener(Screen host, final Callback<Iterable<ChatModelSegment>> listener) {
        listenerCount++;
        Logger.log("ChatBroadcaster", "Listener registered; " + listenerCount + " connected");

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(host.getActivity().getApplicationContext());
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("updates")) {
                    Iterable<ChatModelSegment> segments = (Iterable<ChatModelSegment>) intent.getSerializableExtra("updates");
                    listener.execute(segments);
                }
            }
        };
        broadcastManager.registerReceiver(receiver, new IntentFilter(CHAT_SEGMENT_INTENT_TAG));
        return receiver;
    }

    public static void unregisterListener(Screen host, BroadcastReceiver receiver) {
        listenerCount--;
        Logger.log("ChatBroadcaster", "Listener unregistered; " + listenerCount + " remaining");
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(host.getActivity().getApplicationContext());
        broadcastManager.unregisterReceiver(receiver);
    }

    private void createNewChat(Session session) {
        Logger.log("ChatBroadcaster", "Connecting to chat with " + session);

        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }

        ChatModel chatLocal = new ChatModel(session);
        chatLocal.attachView(context);
        chatLocal.attachCallback(new Callback<Iterable<ChatModelSegment>>() {
            @Override
            public void execute(Iterable<ChatModelSegment> segments) {
                for (ChatModelSegment segment : segments) {
                    Logger.log("ChatBroadcaster", "Broadcast " + segment);
                    segment.visit(chatMonitor);
                }
                Intent intent = new Intent(CHAT_SEGMENT_INTENT_TAG);
                intent.putExtra("updates", (Serializable) segments); //TODO: this is bad. Though segments is really an ArrayList anyway...
                broadcastManager.sendBroadcast(intent);
            }
        });
        chat = chatLocal;
    }

    protected void stop() {
        Logger.log("ChatBroadcaster", "Chat stopped");
        if (this.updateTimer != null) {
            this.updateTimer.cancel();
            this.updateTimer = null;
        }
    }
}
