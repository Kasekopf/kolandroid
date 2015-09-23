package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.models.MessageModel;
import com.github.kolandroid.kol.model.models.chat.raw.RawAction;
import com.github.kolandroid.kol.model.models.chat.raw.RawActionList;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class ChatModelSegment implements Serializable {
    private static final Regex CHANNEL = new Regex(
            "<br>&nbsp;&nbsp;(.*?)(?=<br>|$)", 1);

    private static final Regex INITIAL_MESSAGES = new Regex(
            "handleMessage\\((\\{.*?\\})(, true)?\\);", 1);
    private static final Regex ACTIONS = new Regex(
            "var actions ?= ?(\\{.*?\\});\n", 1);

    private static final Regex PLAYER_ID = new Regex(
            "playerid ?= ?[\"']?(\\d+)[\"']?[,;]", 1);
    private static final Regex PWD = new Regex(
            "pwdhash ? ?= ?[\"']?([0-9a-fA-F]+)[\"']?[,;]", 1);
    private static final Regex BASE_ROOM = new Regex("active: \"([^\"]*)\"", 1);

    public static ArrayList<ChatModelSegment> processChatStart(Session session, Gson parser, ServerReply reply) {
        ArrayList<ChatModelSegment> result = new ArrayList<>();

        if (reply == null) {
            Logger.log("ChatModel", "Unable to connect to KoL");
            MessageModel message = new MessageModel("Unable to connect to KoL.", MessageModel.ErrorType.ERROR);
            result.add(new AssertChatStartFailed(message));
            return result;
        }

        if (!reply.url.contains("mchat.php")) {
            if (reply.html.contains("<b>Add E-Mail Address</b>")) {
                Logger.log("ChatModel", "Player must first add an email address");
                MessageModel message = new MessageModel(session, "Chat:", "You must first add an email address.", "Add", "sendmessage.php", MessageModel.ErrorType.NONE);
                result.add(new AssertChatStartFailed(message));
                return result;
            } else if (reply.html.contains("town_altar.php")) {
                Logger.log("ChatModel", "Player must first complete the alter of literacy");
                MessageModel message = new MessageModel(session, "Chat:", "You may not enter the chat until you have proven yourself literate. You can do so at the Temple of Literacy in the Big Mountains.", "Go There", "town_altar.php", MessageModel.ErrorType.NONE);
                result.add(new AssertChatStartFailed(message));
                return result;
            } else {
                Logger.log("ChatModel", "mchat.php responded with " + reply.url);
                MessageModel message = new MessageModel("Unable to connect to chat.", MessageModel.ErrorType.ERROR);
                result.add(new AssertChatStartFailed(message));
                return result;
            }
        }

        String playerid = PLAYER_ID.extractSingle(reply.html, "0");
        String pwd = PWD.extractSingle(reply.html, "0");
        String visibleChannel = BASE_ROOM.extractSingle(reply.html, "");

        String actionList = ACTIONS.extractSingle(reply.html, "");
        ArrayList<ChatAction> baseActions = new ArrayList<>();
        if (actionList != null) {
            RawActionList rawActions = parser.fromJson(actionList,
                    RawActionList.class);
            for (RawAction raw : rawActions.actions) {
                baseActions.add(new ChatAction(session, raw));
            }
        }

        baseActions.add(0, new ChatAction(session, RawAction.SHOW_PROFILE));

        result.add(new ChatModelSegment.AssertChatStarted(playerid, pwd, visibleChannel, baseActions));

        for (String message : INITIAL_MESSAGES.extractAllSingle(reply.html)) {
            if (message.contains("<span class=\"welcome\">"))
                continue;
            Logger.log("ChatModel", message);
            if (message.equals("{type: 'event', msg: 'Oops!  Sorry, Dave, you appear to be ' + parts[1]}"))
                continue; //ignore this message

            ChatText messageText = parser.fromJson(message, ChatText.class);
            ChatModelSegment messageUpdate = ChatModelSegment.disassembleMessage(new HashSet<Integer>(), messageText);
            if (messageUpdate != null) {
                result.add(messageUpdate);
            }
        }

        return result;
    }

    public static ArrayList<ChatModelSegment> disassembleSegments(Gson parser, HashSet<Integer> seenMessages, ServerReply response, boolean hidden) {
        ArrayList<ChatModelSegment> segments = new ArrayList<>();

        if (response == null) {
            Logger.log("ChatModel", "Received null update");
            return segments;
        }

        if (response.html.length() < 5 || (!response.url.contains("newchatmessages.php")
                && !response.url.contains("submitnewchat.php"))) {
            segments.add(AssertChatClosed.ONLY);
            return segments;
        }

        RawMessageList update = parser.fromJson(response.html,
                RawMessageList.class);

        for (ChatText message : update.messages) {
            ChatModelSegment messageSegment = disassembleMessage(seenMessages, message);
            if (messageSegment != null) {
                segments.add(messageSegment);
            }
        }

        if (update.output != null) {
            ChatModelSegment commandSegment = disassembleCommand(update.output, hidden);
            if (commandSegment == null && !hidden) {
                commandSegment = disassembleMessage(seenMessages, new ChatText(update.output));
            }
            if (commandSegment != null) {
                segments.add(commandSegment);
            }
        }

        if (update.last != null) {
            segments.add(new AssertNewTime(update.last));
        }

        return segments;
    }

    public static ChatModelSegment disassembleMessage(HashSet<Integer> seenMessages, ChatText message) {
        if (message.getID() != 0) {
            if (seenMessages.contains(message.getID()))
                return null;
        }

        return new AssertNewMessage(message);
    }

    private static ChatModelSegment disassembleCommand(String output, boolean hidden) {
        output = output.replace("</font>", "");

        if (output.contains("<font color=green>Available channels:")) {
            return new AssertAvailableChannels(CHANNEL.extractAllSingle(output));
        } else if (output
                .contains("<font color=green>Currently listening to channels:")) {
            ArrayList<String> channels = CHANNEL.extractAllSingle(output);
            for (int i = 0; i < channels.size(); i++) {
                if (channels.get(i).contains("<b>")) {
                    channels.set(i, channels.get(i).replace("<b>", "").replace("</b>", ""));
                }
            }

            return new AssertCurrentChannels(channels);
        }

        Logger.log("ChatModelSegment", "Unknown command: " + output);
        return null;
    }

    public abstract void visit(ChatModelSegmentProcessor processor);

    protected abstract boolean applyToStubs();

    public interface ChatModelSegmentProcessor {
        void chatClosed();

        void setLastTime(String time);

        void receiveMessage(ChatText message);

        void setAvailableChannels(ArrayList<String> channels);

        void setCurrentChannels(ArrayList<String> channels);

        void executeCommand(ChatModel.ChatModelCommand command);

        void startChat(String playerId, String pwd, String visibleChannel, ArrayList<ChatAction> baseActions);

        void startChatFailed(MessageModel message);

        void duplicateModel(ChatModel model);

        void duplicateChannel(ChannelModel channel);
    }

    private static final class AssertChatClosed extends ChatModelSegment {
        public static final AssertChatClosed ONLY = new AssertChatClosed();

        private AssertChatClosed() {

        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.chatClosed();
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }

        @Override
        public String toString() {
            return "$chat.AssertChatClosed[]";
        }
    }

    private static final class AssertNewTime extends ChatModelSegment {
        private final String time;

        private AssertNewTime(String time) {
            this.time = time;
        }

        @Override
        protected boolean applyToStubs() {
            return false;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.setLastTime(time);
        }

        @Override
        public String toString() {
            return "$chat.AssertNewTime[" + time + "]";
        }
    }

    private static final class AssertNewMessage extends ChatModelSegment {
        private final ChatText message;

        private AssertNewMessage(ChatText message) {
            this.message = message;
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.receiveMessage(message);
        }

        @Override
        public String toString() {
            return "$chat.AssertNewMessage[...]";
        }
    }

    private static final class AssertAvailableChannels extends ChatModelSegment {
        private final ArrayList<String> channels;

        private AssertAvailableChannels(ArrayList<String> channels) {
            this.channels = channels;
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.setAvailableChannels(channels);
        }

        @Override
        public String toString() {
            return "$chat.AssertAvailableChannels[" + channels.size() + "]";
        }
    }

    private static final class AssertCurrentChannels extends ChatModelSegment {
        private final ArrayList<String> channels;

        private AssertCurrentChannels(ArrayList<String> channels) {
            this.channels = channels;
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.setCurrentChannels(channels);
        }

        @Override
        public String toString() {
            return "$chat.AssertCurrentChannels[" + channels.size() + "]";
        }
    }

    public static final class ExecuteCommand extends ChatModelSegment {
        private final ChatModel.ChatModelCommand command;

        public ExecuteCommand(ChatModel.ChatModelCommand command) {
            this.command = command;
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.executeCommand(command);
        }

        @Override
        public String toString() {
            return "$chat.ExecuteCommand[" + command + "]";
        }
    }

    private static final class AssertChatStarted extends ChatModelSegment {
        private final String playerId;
        private final String pwd;
        private final String visibleChannel;
        private final ArrayList<ChatAction> baseActions;

        public AssertChatStarted(String playerId, String pwd, String visibleChannel, ArrayList<ChatAction> baseActions) {
            this.playerId = playerId;
            this.pwd = pwd;
            this.visibleChannel = visibleChannel;
            this.baseActions = baseActions;
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.startChat(playerId, pwd, visibleChannel, baseActions);
        }

        @Override
        public String toString() {
            return "$chat.AssertChatStarted[" + visibleChannel + "]";
        }
    }

    public static final class AssertChatStartFailed extends ChatModelSegment {
        private final MessageModel message;

        public AssertChatStartFailed(MessageModel message) {
            this.message = message;
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.startChatFailed(message);
        }

        @Override
        public String toString() {
            return "$chat.AssertChatStartedFailed[" + message + "]";
        }
    }

    protected static final class DuplicateModel extends ChatModelSegment {
        private final ChatModel model;

        protected DuplicateModel(ChatModel model) {
            this.model = model;
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.duplicateModel(model);
        }

        @Override
        public String toString() {
            return "$chat.DuplicateModel[" + model + "]";
        }
    }

    protected static final class DuplicateChannel extends ChatModelSegment {
        private final ChannelModel channel;

        public DuplicateChannel(ChannelModel channel) {
            this.channel = channel;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.duplicateChannel(channel);
        }

        @Override
        protected boolean applyToStubs() {
            return true;
        }
    }

    public static class RawMessageList {
        @SerializedName("msgs")
        public ChatText[] messages;
        public String last;
        public String output;
    }
}
