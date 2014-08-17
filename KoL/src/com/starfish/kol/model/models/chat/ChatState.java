package com.starfish.kol.model.models.chat;

import java.util.ArrayList;

public class ChatState {
	private final ArrayList<ChannelModel> channels;

	public ChatState(ArrayList<ChannelModel> channels) {
		this.channels = channels;
	}

	public ArrayList<ChannelModel> getChannels() {
		return channels;
	}
}
