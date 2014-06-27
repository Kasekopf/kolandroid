package com.starfish.kol.android.chat;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.adapters.ListAdapter;
import com.starfish.kol.android.util.listbuilders.ChannelBuilder;
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.model.interfaces.DeferredAction;
import com.starfish.kol.model.models.chat.ChatChannel;
import com.starfish.kol.model.models.chat.ChatModel;

public class ChatChannelDialog extends DialogFragment {
	public static ChatChannelDialog create(ArrayList<ChatChannel> base) {
		ChatChannelDialog dialog = new ChatChannelDialog();
		Bundle args = new Bundle();
		
		args.putSerializable("base", base);
		dialog.setArguments(args);
		return dialog;
	}

	private ListAdapter<ChatChannel> adapter;
	private OnListSelection<DeferredAction<ChatModel>> selector;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle("Available Channels");
		return dialog;
	}

	public void updateChannels(ArrayList<ChatChannel> channels) {
		this.getArguments().putSerializable("base", channels);
		
		if(adapter != null) {
			adapter.setElements(channels);
		}
	}
	
	public void setOnSelection(OnListSelection<DeferredAction<ChatModel>> select) {
		this.selector = select;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = (View)inflater.inflate(R.layout.dialog_chat_screen,
				container, false);

		@SuppressWarnings("unchecked")
		ArrayList<ChatChannel> base = (ArrayList<ChatChannel>)this.getArguments().getSerializable("base");

	    adapter = new ListAdapter<ChatChannel>(this.getActivity(), base, new ChannelBuilder(selector));
	    
	    ListView list = (ListView)rootView.findViewById(R.id.dialog_chat_list);
	    list.setAdapter(adapter);
		
		return rootView;
	}
}
