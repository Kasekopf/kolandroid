package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.request.SimulatedRequest;
import com.github.kolandroid.kol.request.TentativeRequest;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
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

        seenMessages = new HashSet<Integer>();
        messages = new ArrayList<ChatText>();
        channels = new ArrayList<ChannelModel>();
        channelsByName = new HashMap<String, ChannelModel>();

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

        seenMessages = new HashSet<Integer>();
        messages = new ArrayList<ChatText>();
        channels = new ArrayList<ChannelModel>();
        channelsByName = new HashMap<String, ChannelModel>();

        lasttime = "0";

        this.playerid = PLAYER_ID.extractSingle(reply.html, "0");
        this.pwd = PWD.extractSingle(reply.html, "0");
        this.visibleChannel = BASEROOM.extractSingle(reply.html, "");

        String actionList = ACTIONS.extractSingle(reply.html, "");
        if (actionList == null) {
            this.baseActions = new ArrayList<>();
        } else {
            RawActionList rawActions = parser.fromJson(actionList,
                    RawActionList.class);
            this.baseActions = rawActions.actions;
        }

        baseActions.add(0, ChatAction.SHOWPROFILE);

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

    public static void start(Session session, Callback<ChatModel> onStart) {
        final WeakReference<Callback<ChatModel>> callback = new WeakReference<Callback<ChatModel>>(onStart);

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

                Callback<ChatModel> onStart = callback.get();
                if (onStart == null) {
                    Logger.log("ChatModel", "Chat started, but callback to service has been closed");
                    return;
                }

                ChatModel model = new ChatModel(session, response);
                onStart.execute(model);
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
            submitChat("/channels", true);
            submitChat("/l", true);
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

    private void reassemble(ChatModelSegment chatModelSegment) {
        chatModelSegment.visit(new ChatModelSegment.ChatModelSegmentProcessor() {
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
                ArrayList<ChannelModel> active = new ArrayList<ChannelModel>();
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
            public void submitChatMessage(String message) {
                submitChat(message, false);
            }

            @Override
            public void leaveChannel(String channel) {
                ChannelModel model = getOrCreateChannel(channel);
                model.setActive(false);
            }
        });
    }

    public void submitChat(String channel, String msg) {
        log("Submitting " + msg + " to " + channel);
        if (!msg.startsWith("/")) {
            if (channel.startsWith("@")) {
                // private messaging channel
                msg = String
                        .format("/msg %s %s", channel.replace("@", ""), msg);
            } else {
                msg = String.format("/c %s %s", channel, msg);
            }
        }
        submitChat(msg);
    }

    protected void submitChat(String msg) {
        submitChat(msg, false);
    }

    public void triggerUpdate() {
        Request r = new Request("newchatmessages.php?j=1&lasttime=" + lasttime);
        this.makeRequest(r, new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                ArrayList<ChatModelSegment> newSegments = ChatModelSegment.disassembleSegments(parser, seenMessages, response, false);
                if (newSegments.size() > 0) {
                    apply(newSegments);
                }
            }
        });
    }

    protected void submitChat(String msg, final boolean hiddenCommand) {
        String url = encodeChatMessage("submitnewchat.php?playerid=%s&pwd=%s&graf=%s&j=1", playerid, pwd, msg);
        Logger.log("ChatModel", "Submitting chat for " + url);

        Request req = new Request(url);
        this.makeRequest(req, new ResponseHandler() {
            @Override
            public void handle(Session session, ServerReply response) {
                ArrayList<ChatModelSegment> newSegments = ChatModelSegment.disassembleSegments(parser, seenMessages, response, hiddenCommand);
                if (newSegments.size() > 0) {
                    apply(newSegments);
                }
            }
        });
    }

    protected void makeRequest(Request req) {
        super.makeRequest(req);
    }

    public void displayRejectionMessage() {
        String html = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"https://images.kingdomofloathing.com/styles.css\"></head><body><span class=small>You may not enter the chat until you have proven yourself literate. You can do so at the <a target=mainpane href=\"town_altar.php\">Temple of Literacy</a> in the Big Mountains.</body></html>";
        ServerReply reject = new ServerReply(200, "", "", html,
                "chatreject.php?androiddisplay=small", "");
        this.makeRequest(new SimulatedRequest(reject));
    }

    protected boolean stubbed() {
        return false;
    }

    public ArrayList<ChannelModel> getChannels() {
        return new ArrayList<ChannelModel>(channels);
    }

    public ChannelModel getChannel(String name) {
        return channelsByName.get(name);
    }

    public void setCurrentRoom(String room) {
        this.visibleChannel = room;
    }

    protected void leaveChannel(String channel) {
        getOrCreateChannel(channel).setActive(false);
    }

    protected void log(String message) {
        Logger.log("ChatModel", message);
    }

    public static class RawActionList {
        public final ArrayList<ChatAction> actions;

        public RawActionList(ArrayList<ChatAction> actions) {
            this.actions = actions;
        }
    }

    public static class RawActionListDeserializer implements
            JsonDeserializer<RawActionList> {
        @Override
        public RawActionList deserialize(JsonElement element, Type type,
                                         JsonDeserializationContext context) throws JsonParseException {
            ArrayList<ChatAction> actions = new ArrayList<ChatAction>();

            JsonObject jsonObject = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                // For individual City objects, we can use default
                // deserialisation:
                ChatAction action = context.deserialize(entry.getValue(),
                        ChatAction.class);
                action.setEntry(entry.getKey());
                actions.add(action);
            }

            return new RawActionList(actions);
        }
    }
}
