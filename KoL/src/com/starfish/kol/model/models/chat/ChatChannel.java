package com.starfish.kol.model.models.chat;

import java.util.ArrayList;

public class ChatChannel {
	private final ArrayList<ChatText> messages;
	private final String name;

	public ChatChannel(String name) {
		this.name = name;
		this.messages = new ArrayList<ChatText>();
	}

	public String getName() {
		return this.name;
	}
	
	protected void addMessage(ChatText message) {
		messages.add(message);
	}

	public ArrayList<ChatText> getMessages() {
		return new ArrayList<ChatText>(messages);
	}
}