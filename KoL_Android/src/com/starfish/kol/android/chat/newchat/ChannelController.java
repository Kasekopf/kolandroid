package com.starfish.kol.android.chat.newchat;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.binders.ChatBinder;
import com.starfish.kol.android.controller.Controller;
import com.starfish.kol.android.screen.DialogScreen;
import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.screen.ScreenSelection;
import com.starfish.kol.android.util.adapters.ListAdapter;
import com.starfish.kol.model.models.chat.ChannelModel;
import com.starfish.kol.model.models.chat.ChatModel;
import com.starfish.kol.model.models.chat.ChatText;

public class ChannelController implements Controller {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 2772987489714420525L;

	private final String name;
	
	private transient LiveChatConnection connection;
	private transient ChannelModel base = null;
	private transient ListAdapter<ChatText> adapter = null;
	
	public ChannelController(String tag) {
		this.name = tag;
	}
		
	@Override
	public void connect(View view, final Screen host) {
		List<ChatText> messages = (base == null) ? new ArrayList<ChatText>() : base.getMessages();
		adapter = new ListAdapter<ChatText>(view.getContext(), messages, ChatBinder.ONLY);
		
		ListView list = (ListView)view.findViewById(R.id.chatroom_display_list);
		list.setAdapter(adapter);
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
				final ChatText choice = (ChatText)myAdapter.getItemAtPosition(myItemInt);
				
				if(choice.getActions().size() == 0) return;
				
				ChatActionsController controller = new ChatActionsController(choice);
				DialogScreen.display(controller, host);
			}
		});

		connection = new LiveChatConnection(this.getClass().getSimpleName()) {
			@Override
			public void onConnection(ChatModel model) {
				base = model.getChannel(name);
			}

			@Override
			public void recievedRefresh() {
				if(base != null && adapter != null) {
					adapter.setElements(base.getMessages());
				}
			}
		};
		connection.connect(host.getActivity());
	}

	@Override
	public void disconnect(Screen host) {
		if(connection != null)
			connection.close(host.getActivity());
	}

	@Override
	public int getView() {
		return R.layout.fragment_chatroom;
	}

	@Override
	public void chooseScreen(ScreenSelection choice) {
		choice.displayPrimary(this);
	}
}