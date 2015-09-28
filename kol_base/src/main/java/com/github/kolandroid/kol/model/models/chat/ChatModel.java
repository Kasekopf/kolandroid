package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.model.LinkedModel;
import com.github.kolandroid.kol.model.models.MessageModel;
import com.github.kolandroid.kol.model.models.chat.raw.RawActionList;
import com.github.kolandroid.kol.model.models.chat.raw.RawActionListDeserializer;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.request.TentativeRequest;
import com.github.kolandroid.kol.util.Logger;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChatModel extends LinkedModel<Iterable<ChatModelSegment>> {
    private final transient Gson parser;
    private final HashSet<Integer> seenMessages;
    private final ArrayList<ChatText> messages;
    private final Map<String, ChannelModel> channelsByName;
    private final ArrayList<ChannelModel> channels;
    private final ArrayList<ChatAction> baseActions;

    private boolean started;

    private String playerid;
    private String pwd;

    private String visibleChannel;
    private String lasttime;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public ChatModel(Session s) {
        super(s);

        seenMessages = new HashSet<>();
        messages = new ArrayList<>();
        channels = new ArrayList<>();
        channelsByName = new HashMap<>();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(RawActionList.class,
                new RawActionListDeserializer());
        parser = builder.setFieldNamingPolicy(
                FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        playerid = "";
        pwd = "";
        baseActions = new ArrayList<>();
        started = false;
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

    protected void duplicate(ChatModel cloneFrom) {
        seenMessages.clear();
        seenMessages.addAll(cloneFrom.seenMessages);

        messages.clear();
        messages.addAll(cloneFrom.messages);

        for (ChannelModel cloneChannelFrom : cloneFrom.channels) {
            ChannelModel channel = getOrCreateChannel(cloneChannelFrom.getName());
            channel.duplicate(cloneChannelFrom);
        }

        baseActions.clear();
        baseActions.addAll(cloneFrom.baseActions);

        playerid = cloneFrom.playerid;
        pwd = cloneFrom.pwd;
        lasttime = cloneFrom.lasttime;
        changePrimaryChannel(cloneFrom.visibleChannel);
        started = cloneFrom.started;

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
        ArrayList<ChatModelSegment> broadcastSegments = new ArrayList<>();
        for(ChatModelSegment seg : segments) {
            this.reassemble(seg);
            if (seg.applyToStubs()) {
                broadcastSegments.add(seg);
            }
        }
        notifyView(broadcastSegments);
    }

    private void reassemble(ChatModelSegment segment) {
        segment.visit(new ChatModelSegment.ChatModelSegmentProcessor() {
            @Override
            public void chatClosed() {
                started = false;
            }

            @Override
            public void setLastTime(String time) {
                lasttime = time;
            }

            @Override
            public void receiveMessage(ChatText message) {
                seenMessages.add(message.getID());
                message.prepare(getSession(), baseActions, visibleChannel);

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

            @Override
            public void startChat(String playerId, String pwd, String visibleChannel, ArrayList<ChatAction> baseActions) {
                started = true;
                ChatModel.this.playerid = playerId;
                ChatModel.this.pwd = pwd;
                ChatModel.this.visibleChannel = visibleChannel;
                ChatModel.this.baseActions.addAll(baseActions);
            }

            @Override
            public void startChatFailed(MessageModel message) {
                started = false;
            }

            @Override
            public void duplicateModel(ChatModel model) {
                duplicate(model);
            }

            @Override
            public void duplicateChannel(ChannelModel channel) {
                ChannelModel current = getOrCreateChannel(channel.getName());
                current.duplicate(channel);
            }
        });
    }

    public void submitCommand(ChatModelCommand command) {
        ChatModelSegment toBroadcast = command.complete(this);
        if (toBroadcast != null) {
            ArrayList<ChatModelSegment> update = new ArrayList<>();
            update.add(toBroadcast);
            this.notifyView(update);
        }
    }

    protected void fillPartialChatPrompt(String message) {
        // do nothing
    }

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

    public String getCurrentChannel() {
        return visibleChannel;
    }

    public boolean hasStarted() {
        return started;
    }

    private void changePrimaryChannel(String newPrimaryChannel) {
        if (newPrimaryChannel == null) {
            return;
        }

        if (visibleChannel == null) {
            log("Current channel set from NULL to " + newPrimaryChannel);
            visibleChannel = newPrimaryChannel;
            getOrCreateChannel(visibleChannel).notifyPrimaryChanged();
        } else if (!visibleChannel.equals(newPrimaryChannel)) {
            log("Current channel set from " + visibleChannel + " to " + newPrimaryChannel);
            ChannelModel oldChannel = getOrCreateChannel(visibleChannel);
            ChannelModel newChannel = getOrCreateChannel(newPrimaryChannel);
            visibleChannel = newPrimaryChannel;

            oldChannel.notifyPrimaryChanged();
            newChannel.notifyPrimaryChanged();
        }

    }

    public interface ChatModelCommand extends Serializable {
        /**
         * Run the command on the the provided model.
         *
         * @param base Chat model to execute command on
         * @return A segment to broadcast in response, or null if no broadcast should occur
         */
        ChatModelSegment complete(ChatModel base);

        class UpdateChat implements ChatModelCommand {
            public static final UpdateChat ONLY = new UpdateChat();

            @Override
            public ChatModelSegment complete(final ChatModel base) {
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
                return null;
            }
        }

        class SetCurrentChannel implements ChatModelCommand {
            private final String channel;

            public SetCurrentChannel(String channel) {
                this.channel = channel;
            }

            @Override
            public ChatModelSegment complete(ChatModel base) {
                base.changePrimaryChannel(channel);
                return new ChatModelSegment.ExecuteCommand(this);
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
                        message = String.format("/%s %s", channel, message);
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
            public ChatModelSegment complete(final ChatModel base) {
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
                return null;
            }
        }

        class LeaveChannel implements ChatModelCommand {
            private final String channel;

            public LeaveChannel(String channel) {
                this.channel = channel;
            }


            @Override
            public ChatModelSegment complete(ChatModel base) {
                ChannelModel model = base.getOrCreateChannel(channel);
                model.setActive(false);
                return new ChatModelSegment.ExecuteCommand(this);
            }
        }

        class ReadChannelMessages implements ChatModelCommand {
            private final String channel;

            public ReadChannelMessages(String channel) {
                this.channel = channel;
            }

            @Override
            public ChatModelSegment complete(ChatModel base) {
                ChannelModel model = base.getChannel(channel);
                if (model != null) {
                    model.setMessagesRead();
                }
                return new ChatModelSegment.ExecuteCommand(this);
            }
        }

        class FillPartialChat implements ChatModelCommand {
            private final String partial;

            public FillPartialChat(String partial) {
                this.partial = partial;
            }


            @Override
            public ChatModelSegment complete(ChatModel base) {
                Logger.log("ChatModel", base + " filled partial message " + partial);
                base.fillPartialChatPrompt(partial);
                return new ChatModelSegment.ExecuteCommand(this);
            }
        }

        class StartChat implements ChatModelCommand {
            private final Session session;

            public StartChat(Session session) {
                this.session = session;
            }

            public Session getSession() {
                return session;
            }

            @Override
            public ChatModelSegment complete(final ChatModel base) {
                if (!base.started) {
                    Logger.log("ChatModel", "Starting chat with " + base.getSession());
                    Request req = new TentativeRequest("mchat.php", new ResponseHandler() {
                        @Override
                        public void handle(Session session, ServerReply response) {
                            Logger.log("ChatModel", "Unable to start chat");
                            ArrayList<ChatModelSegment> result = new ArrayList<>();
                            MessageModel message = new MessageModel("Unable to connect to KoL.", MessageModel.ErrorType.ERROR);
                            result.add(new ChatModelSegment.AssertChatStartFailed(message));
                            base.apply(result);
                        }
                    });

                    req.makeAsync(base.getSession(), LoadingContext.NONE, new ResponseHandler() {
                        @Override
                        public void handle(Session session, ServerReply response) {
                            ArrayList<ChatModelSegment> result = ChatModelSegment.processChatStart(base.getSession(), base.parser, response);
                            base.apply(result);

                            if (result.size() > 1) {
                                // Only 1 is a failure
                                base.submitCommand(new ChatModelCommand.SubmitChatMessage("/channels", true));
                                base.submitCommand(new ChatModelCommand.SubmitChatMessage("/l", true));
                            }
                        }
                    });
                }
                return null;
            }
        }

        class StopChat implements ChatModelCommand {
            public static final StopChat ONLY = new StopChat();

            private StopChat() {
            }

            @Override
            public ChatModelSegment complete(ChatModel base) {
                base.started = false;
                for (ChannelModel c : base.getChannels()) {
                    c.readAllMessages();
                }
                return ChatModelSegment.AssertChatClosed.ONLY;
            }
        }

        class RequestDuplication implements ChatModelCommand {
            public static final RequestDuplication ONLY = new RequestDuplication();

            private RequestDuplication() {
            }

            @Override
            public ChatModelSegment complete(ChatModel base) {
                return new ChatModelSegment.DuplicateModel(base);
            }
        }

        class RequestChannelDuplication implements ChatModelCommand {
            private final String tag;

            public RequestChannelDuplication(String channelTag) {
                this.tag = channelTag;
            }

            @Override
            public ChatModelSegment complete(ChatModel base) {
                if (tag == null)
                    return null;
                ChannelModel channel = base.getChannel(tag);
                if (channel == null)
                    return null;
                return new ChatModelSegment.DuplicateChannel(channel);
            }
        }

        class RequestCurrentChannel implements ChatModelCommand {
            public static final RequestCurrentChannel ONLY = new RequestCurrentChannel();

            private RequestCurrentChannel() {
            }

            @Override
            public ChatModelSegment complete(ChatModel base) {
                return new ChatModelSegment.ExecuteCommand(new SetCurrentChannel(base.visibleChannel));
            }
        }
    }
}
