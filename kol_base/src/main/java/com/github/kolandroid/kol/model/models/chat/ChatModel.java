package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.model.models.chat.raw.RawAction;
import com.github.kolandroid.kol.model.models.chat.raw.RawActionList;
import com.github.kolandroid.kol.model.models.chat.raw.RawActionListDeserializer;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.request.TentativeRequest;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChatModel extends LinkedModel<Iterable<ChatModelSegment>> {

    private static final Regex INITIAL_MESSAGES = new Regex(
            "handleMessage\\((\\{.*?\\})(, true)?\\);", 1);
    private static final Regex ACTIONS = new Regex(
            "var actions ?= ?(\\{.*?\\});\n", 1);

    private static final Regex PLAYER_ID = new Regex(
            "playerid ?= ?[\"']?(\\d+)[\"']?[,;]", 1);
    private static final Regex PWD = new Regex(
            "pwdhash  ?= ?[\"']?([0-9a-fA-F]+)[\"']?[,;]", 1);
    private static final Regex BASEROOM = new Regex("active: \"([^\"]*)\"", 1);

    private final Gson parser;
    private final HashSet<Integer> seenMessages;
    private final ArrayList<ChatText> messages;
    private final Map<String, ChannelModel> channelsByName;
    private final ArrayList<ChannelModel> channels;
    private final ArrayList<ChatAction> baseActions;

    private String playerid;
    private String pwd;

    private String visibleChannel;
    private String lasttime;

    protected ChatModel(Session s) {
        super(s);

        seenMessages = new HashSet<>();
        messages = new ArrayList<>();
        channels = new ArrayList<>();
        channelsByName = new HashMap<>();

        parser = null;

        playerid = "";
        pwd = "";
        baseActions = new ArrayList<>();
    }

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public ChatModel(Session s, ServerReply reply) {
        super(s);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(RawActionList.class,
                new RawActionListDeserializer());
        parser = builder.setFieldNamingPolicy(
                FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        seenMessages = new HashSet<>();
        messages = new ArrayList<>();
        channels = new ArrayList<>();
        channelsByName = new HashMap<>();

        lasttime = "0";

        this.playerid = PLAYER_ID.extractSingle(reply.html, "0");
        this.pwd = PWD.extractSingle(reply.html, "0");
        this.visibleChannel = BASEROOM.extractSingle(reply.html, "");

        String actionList = ACTIONS.extractSingle(reply.html, "");
        this.baseActions = new ArrayList<>();
        if (actionList != null) {
            RawActionList rawActions = parser.fromJson(actionList,
                    RawActionList.class);
            for (RawAction raw : rawActions.actions) {
                this.baseActions.add(new ChatAction(s, raw));
            }
        }

        baseActions.add(0, new ChatAction(getSession(), RawAction.SHOWPROFILE));

        for (String message : INITIAL_MESSAGES.extractAllSingle(reply.html)) {
            if (message.contains("<span class=\"welcome\">"))
                continue;
            Logger.log("ChatModel", message);
            if (message.equals("{type: 'event', msg: 'Oops!  Sorry, Dave, you appear to be ' + parts[1]}"))
                continue; //ignore this message

            ChatText messageText = parser.fromJson(message, ChatText.class);
            ChatModelSegment messageUpdate = ChatModelSegment.disassembleMessage(seenMessages, messageText);
            if (messageUpdate != null)
                this.reassemble(messageUpdate);
        }

    }

    public static void start(Session session, final Callback<ChatModel> onStart) {
        Request req = new TentativeRequest("mchat.php", new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                Logger.log("ChatModel", "Unable to start chat");
            }
        });

        req.makeAsync(session, LoadingContext.NONE, new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                if (!response.url.contains("mchat.php"))
                    return;
                Logger.log("ChatModel", "Chat started");


                onStart.execute(new ChatModel(session, response));
            }
        });
    }

    @SuppressWarnings("deprecation")
    public static String encodeChatMessage(String baseUrl, String playerId, String pwd, String msg) {
        String encodedMsg;

        try {
            encodedMsg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedMsg = URLEncoder.encode(msg);
        }

        return String.format(baseUrl, playerId, pwd, encodedMsg);
    }

    @Override
    public void attachView(ViewContext context) {
        super.attachView(context);

        if (!this.stubbed()) {
            //Update internal list of channels.
            this.submitCommand(new ChatModelCommand.SubmitChatMessage("/channels", true));
            this.submitCommand(new ChatModelCommand.SubmitChatMessage("/l", true));
        }
    }

    public void duplicate(ChatModel cloneFrom) {
        seenMessages.clear();
        seenMessages.addAll(cloneFrom.seenMessages);

        messages.clear();
        messages.addAll(cloneFrom.messages);

        channels.clear();
        channelsByName.clear();
        for (ChannelModel cloneChannelFrom : cloneFrom.channels) {
            ChannelModel channel = new ChannelModel(cloneChannelFrom, this);
            channels.add(channel);
            channelsByName.put(channel.getName(), channel);
        }

        baseActions.clear();
        baseActions.addAll(cloneFrom.baseActions);

        playerid = cloneFrom.playerid;
        pwd = cloneFrom.pwd;
        lasttime = cloneFrom.lasttime;
        visibleChannel = cloneFrom.visibleChannel;

        notifyView(new ArrayList<ChatModelSegment>());
    }

    private ChannelModel getOrCreateChannel(String name) {
        ChannelModel channel;
        if (channelsByName.containsKey(name)) {
            channel = channelsByName.get(name);
        } else {
            channel = new ChannelModel(this, name, this.getSession());
            channels.add(channel);
            channelsByName.put(name, channel);
            log("Added new chat channel: " + name);
        }
        return channel;
    }

    public void apply(Iterable<ChatModelSegment> segments) {
        for(ChatModelSegment seg : segments) {
            this.reassemble(seg);
        }
        notifyView(segments);
    }

    private void reassemble(ChatModelSegment segment) {
        segment.visit(new ChatModelSegment.ChatModelSegmentProcessor() {
            @Override
            public void chatClosed() {

            }

            @Override
            public void setLastTime(String time) {
                lasttime = time;
            }

            @Override
            public void receiveMessage(ChatText message) {
                seenMessages.add(message.getID());
                message.prepare(baseActions, visibleChannel);

                String channelName = message.getChannel();
                ChannelModel channel = getOrCreateChannel(channelName);
                messages.add(message);
                channel.addMessage(message);
            }

            @Override
            public void setAvailableChannels(ArrayList<String> channels) {
                for (String channel : channels)
                    getOrCreateChannel(channel);
            }

            @Override
            public void setCurrentChannels(ArrayList<String> channels) {
                ArrayList<ChannelModel> active = new ArrayList<>();
                for (String channel : channels) {
                    ChannelModel model = getOrCreateChannel(channel);
                    model.setActive(true);
                    active.add(model);
                }

                for (ChannelModel channel : ChatModel.this.channels) {
                    channel.setActive(active.contains(channel));
                }
            }

            @Override
            public void executeCommand(ChatModelCommand command) {
                command.complete(ChatModel.this); // do not rebroadcast
            }
        });
    }

    public void submitCommand(ChatModelCommand command) {
        boolean rebroadcast = command.complete(this);
        if (rebroadcast) {
            ArrayList<ChatModelSegment> update = new ArrayList<>();
            update.add(new ChatModelSegment.ExecuteCommand(command));
            this.notifyView(update);
        }
    }

    /*
    public void displayRejectionMessage() {
        String html = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"https://images.kingdomofloathing.com/styles.css\"></head><body><span class=small>You may not enter the chat until you have proven yourself literate. You can do so at the <a target=mainpane href=\"town_altar.php\">Temple of Literacy</a> in the Big Mountains.</body></html>";
        ServerReply reject = new ServerReply(200, "", "", html,
                "chatreject.php?androiddisplay=small", "");
        this.makeRequest(new SimulatedRequest(reject));
    }
    */

    protected boolean stubbed() {
        return false;
    }

    public int getMessageCount() {
        return messages.size();
    }

    public ArrayList<ChannelModel> getChannels() {
        return new ArrayList<>(channels);
    }

    public ChannelModel getChannel(String name) {
        return channelsByName.get(name);
    }

    protected void log(String message) {
        Logger.log("ChatModel", message);
    }

    public interface ChatModelCommand {
        /**
         * Run the command on the the provided model.
         *
         * @param base Chat model to execute command on
         * @return True if the chat model should rebroadcast the command
         */
        boolean complete(ChatModel base);

        class UpdateChat implements ChatModelCommand {
            public static final UpdateChat ONLY = new UpdateChat();

            @Override
            public boolean complete(final ChatModel base) {
                Request r = new Request("newchatmessages.php?j=1&lasttime=" + base.lasttime);
                base.makeRequest(r, new ResponseHandler() {
                    @Override
                    public void handle(Session session, ServerReply response) {
                        ArrayList<ChatModelSegment> newSegments = ChatModelSegment.disassembleSegments(base.parser, base.seenMessages, response, false);
                        if (newSegments.size() > 0) {
                            base.apply(newSegments);
                        }
                    }
                });
                return false;
            }
        }

        class SetCurrentChannel implements ChatModelCommand {
            private final String channel;

            public SetCurrentChannel(String channel) {
                this.channel = channel;
            }

            @Override
            public boolean complete(ChatModel base) {
                base.visibleChannel = channel;
                return true;
            }
        }

        class SubmitChatMessage implements ChatModelCommand {
            private final String message;
            private final boolean hidden;

            public SubmitChatMessage(String channel, String message) {
                if (!message.startsWith("/")) {
                    if (channel.startsWith("@")) {
                        // private messaging channel
                        message = String
                                .format("/msg %s %s", channel.replace("@", ""), message);
                    } else {
                        message = String.format("/c %s %s", channel, message);
                    }
                }

                this.message = message;
                this.hidden = false;
            }

            public SubmitChatMessage(String message) {
                this.message = message;
                this.hidden = false;
            }

            public SubmitChatMessage(String message, boolean hidden) {
                this.message = message;
                this.hidden = hidden;
            }

            @Override
            public boolean complete(final ChatModel base) {
                String url = encodeChatMessage("submitnewchat.php?playerid=%s&pwd=%s&graf=%s&j=1", base.playerid, base.pwd, message);
                Logger.log("ChatModel", "Submitting chat for " + url);

                Request req = new Request(url);
                base.makeRequest(req, new ResponseHandler() {
                    @Override
                    public void handle(Session session, ServerReply response) {
                        ArrayList<ChatModelSegment> newSegments = ChatModelSegment.disassembleSegments(base.parser, base.seenMessages, response, hidden);
                        if (newSegments.size() > 0) {
                            base.apply(newSegments);
                        }
                    }
                });
                return false;
            }
        }

        class LeaveChannel implements ChatModelCommand {
            private final String channel;

            public LeaveChannel(String channel) {
                this.channel = channel;
            }


            @Override
            public boolean complete(ChatModel base) {
                ChannelModel model = base.getOrCreateChannel(channel);
                model.setActive(false);
                return true;
            }
        }

        class ReadChannelMessages implements ChatModelCommand {
            private final String channel;

            public ReadChannelMessages(String channel) {
                this.channel = channel;
            }

            @Override
            public boolean complete(ChatModel base) {
                ChannelModel model = base.getChannel(channel);
                if (model != null) {
                    model.setMessagesRead();
                }
                return true;
            }
        }
    }
}
