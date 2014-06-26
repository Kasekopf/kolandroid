package com.starfish.kol.model.models.chat;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ChatText implements Serializable {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 6832877127537757279L;

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

	private final ArrayList<ChatAction> actions;
	
	public ChatText() {
		actions = new ArrayList<ChatAction>();
	}
	
	public ChatText(String htmltext) {
		this();
		
		this.content = htmltext;
		this.format = -1;
		this.mid = 0;		
	}
	
	protected void prepare(ArrayList<ChatAction> newActions, String defaultChannel){
		if(newActions != null && user != null)
			actions.addAll(newActions);
		
		if(baseChannel == null || baseChannel.length() == 0)
			this.baseChannel = defaultChannel;
	}
	
	public ArrayList<ChatAction> getActions() {
		return actions;
	}
	
	protected String getChannel() {		
		if(type != null && type.contentEquals("private")) {
			if(privateWith != null) {
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
	
	public String getTitle() {
		if(user == null) return "";
		if(user.name == null) return "";
		return user.name;
	}

	public String getText() {
		switch (format) {
		case -1:
			//preformatted message
			break;
		case 1: // emote
			return content;
		case 2: // system
			return "<font color='red'>" + content + "</font>";
		case 3: // mod warning
			return "<font color='red'><b>" + user.getName(false) + "</b>: "
					+ content + "</font>";
		case 4: // mod announcment
			return "<font color='green'>" + content + "</font>";
		case 98: // event
			return content;
		case 99: // welcome
			return "<font color='green'><i>" + content + "</i></font>";
		default:
			System.err.println("Chat recieved unknown format: " + format);
		case 0: // player message
			break;
		}

		if (user == null)
			return content;
		return "<b>" + user.getName(true) + "</b>: " + content;
	}
	
	public static class ChatUser implements Serializable {
		/**
		 * Autogenerated by eclipse.
		 */
		private static final long serialVersionUID = 2170535950761375904L;

		private String name;
		private int id;
		private String color;

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


