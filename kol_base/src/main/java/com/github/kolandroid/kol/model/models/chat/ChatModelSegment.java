package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class ChatModelSegment implements Serializable {
    private static final Regex CHANNEL = new Regex(
            "<br>&nbsp;&nbsp;(.*?)(?=<br>|$)", 1);

    public static ArrayList<ChatModelSegment> disassembleSegments(Gson parser, HashSet<Integer> seenMessages, ServerReply response, boolean hidden) {
        ArrayList<ChatModelSegment> segments = new ArrayList<>();

        if (response == null) {
            Logger.log("ChatModel", "Recieved null updated");
            return segments;
        }

        if (response.html.length() < 5 || (!response.url.contains("newchatmessages.php")
                && !response.url.contains("submitnewchat.php"))) {
            segments.add(new AssertChatClosed());
            return segments;
        }

        RawMessageList update = parser.fromJson(response.html,
                RawMessageList.class);

        boolean updated = false;
        for (ChatText message : update.msgs) {
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


    public interface ChatModelSegmentProcessor {
        void chatClosed();

        void setLastTime(String time);

        void receiveMessage(ChatText message);

        void setAvailableChannels(ArrayList<String> channels);

        void setCurrentChannels(ArrayList<String> channels);

        void submitChatMessage(String message);

        void leaveChannel(String channel);
    }

    private static final class AssertChatClosed extends ChatModelSegment {
        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.chatClosed();
        }
    }

    private static final class AssertNewTime extends ChatModelSegment {
        private final String time;

        private AssertNewTime(String time) {
            this.time = time;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.setLastTime(time);
        }
    }

    private static final class AssertNewMessage extends ChatModelSegment {
        private final ChatText message;

        private AssertNewMessage(ChatText message) {
            this.message = message;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.receiveMessage(message);
        }
    }

    private static final class AssertAvailableChannels extends ChatModelSegment {
        private final ArrayList<String> channels;

        private AssertAvailableChannels(ArrayList<String> channels) {
            this.channels = channels;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.setAvailableChannels(channels);
        }
    }

    private static final class AssertCurrentChannels extends ChatModelSegment {
        private final ArrayList<String> channels;

        private AssertCurrentChannels(ArrayList<String> channels) {
            this.channels = channels;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.setCurrentChannels(channels);
        }
    }

    public static final class SubmitChatMessage extends ChatModelSegment {
        private final String message;

        public SubmitChatMessage(String message) {
            this.message = message;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.submitChatMessage(message);
        }
    }

    public static final class LeaveChannel extends ChatModelSegment {
        private final String channel;

        public LeaveChannel(String channel) {
            this.channel = channel;
        }

        @Override
        public void visit(ChatModelSegmentProcessor processor) {
            processor.leaveChannel(channel);
        }
    }

    public static class RawMessageList {
        public ChatText[] msgs;
        public String last;
        public String output;
    }
}
