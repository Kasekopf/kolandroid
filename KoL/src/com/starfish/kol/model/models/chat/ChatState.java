package com.starfish.kol.model.models.chat;

import java.util.ArrayList;

public class ChatState {
	private final ArrayList<ChatChannel> channels;

	public ChatState(ArrayList<ChatChannel> channels) {
		this.channels = channels;
	}

	public ArrayList<ChatChannel> getChannels() {
		return channels;
	}
}
