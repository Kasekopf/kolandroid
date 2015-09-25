package com.github.kolandroid.kol.model.models.chat;

import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatText implements Serializable {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 6832877127537757279L;

    private static final Regex LINKS = new Regex("<a[^>]*href=[\"']?([^\"'> ]*)[\"' >]", 1);

    private static final Regex WHOIS = new Regex("<a[^>]*showplayer.php[^>]*><b[^>]*>([^<]*)</b></a>,", 1);
    private static final Regex WHOIS_USER = new Regex("(.*) ", 1);
    private static final Regex WHOIS_ID = new Regex("\\(#(\\d+)\\)", 1);


    private final ArrayList<ChatAction> actions;
    @SerializedName("msg")
    private String content;
    @SerializedName("channel")
    private String baseChannel;
    private int mid;
    private int format;
    @SerializedName("who")
    private ChatUser user;
    @SerializedName("for")
    private ChatUser privateWith;
    private String type;

    public ChatText() {
        actions = new ArrayList<>();
    }

    public ChatText(String htmlText) {
        this();

        this.content = htmlText;
        this.format = -1;
        this.mid = 0;

        //If this is a "whois" message, extract it
        try {
            String whois = WHOIS.extractSingle(this.content, null);
            if (whois != null) {
                String name = WHOIS_USER.extractSingle(whois, "");
                int id = Integer.parseInt(WHOIS_ID.extractSingle(whois, "0"));
                if (!name.isEmpty() && id != 0) {
                    this.user = new ChatUser(name, id);
                }

                //remove the link to the profile to avoid duplicating the SHOWPROFILE action
                this.content = WHOIS.replaceAll(content, "<font color=green>$1,</font>");
            }
        } catch (NumberFormatException e) {
            //unable to parse the whois message
        }
    }

    protected void prepare(Session session, ArrayList<ChatAction> newActions, String defaultChannel) {
        if (actions.size() == 0) {
            if (newActions != null && user != null)
                actions.addAll(newActions);
            Set<String> links = new HashSet<String>();
            Logger.log("ChatText", content);
            for (String match : LINKS.extractAllSingle(content)) {
                if (links.contains(match)) continue;
                links.add(match);

                Logger.log("ChatText", "  Located link: " + match);
                actions.add(new ChatLink(session, match));
            }
        }

        if (baseChannel == null || baseChannel.length() == 0)
            this.baseChannel = defaultChannel;
    }

    public ArrayList<ChatAction> getActions() {
        return actions;
    }

    protected String getChannel() {
        if (type != null && type.contentEquals("private")) {
            if (privateWith != null) {
                return "@" + privateWith.name.toLowerCase();
            } else {
                return "@" + user.name.toLowerCase();
            }
        }

        if (baseChannel == null || baseChannel.length() == 0) {
            return "unknown";
        }
        return baseChannel.toLowerCase();
    }

    protected int getID() {
        return mid;
    }

    protected ChatUser getUser() {
        return user;
    }

    protected boolean isEvent() {
        return this.format == 98;
    }

    public String getTitle() {
        if (user == null) return "";
        if (user.name == null) return "";
        return user.name;
    }

    public String getText() {
        switch (format) {
            case -1:
                return content;
            case 1: // emote
                return content;
            case 2: // system
                return "<font color='red'>" + content + "</font>";
            case 3: // mod warning
                return "<font color='red'><b>" + user.getName(false) + "</b>: "
                        + content + "</font>";
            case 4: // mod announcement
                return "<font color='green'>" + content + "</font>";
            case 98: // event
                return content;
            case 99: // welcome
                return "<font color='green'><i>" + content + "</i></font>";
            default:
                Logger.log("ChatText", "Chat received unknown format: " + format);
            case 0: // player message
                if (user == null)
                    return content;
                return "<b>" + user.getName(true) + "</b>: " + content;
        }
    }

    public static class ChatUser implements Serializable {
        /**
         * Autogenerated by eclipse.
         */
        private static final long serialVersionUID = 2170535950761375904L;

        private String name;
        private int id;
        private String color;

        public ChatUser(String name, int id) {
            this.name = name;
            this.id = id;
            this.color = "black";
        }

        protected String getId() {
            return id + "";
        }

        protected String getName() {
            return name;
        }

        public String getName(boolean applyColor) {
            if (!applyColor || color == null || color.length() == 0)
                return name;
            else
                return "<font color='" + color + "'>" + name + "</font>";
        }
    }
}


