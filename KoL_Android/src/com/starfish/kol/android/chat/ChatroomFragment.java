package com.starfish.kol.android.chat;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.binders.ChatBinder;
import com.starfish.kol.android.util.adapters.ListAdapter;
import com.starfish.kol.model.models.chat.ChannelModel;
import com.starfish.kol.model.models.chat.ChatAction;
import com.starfish.kol.model.models.chat.ChatText;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Use the
 * {@link ChatroomFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class ChatroomFragment extends Fragment {
	private ChannelModel base;
	private ListAdapter<ChatText> adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

		this.base = (ChannelModel)getArguments().getSerializable("base");
		
		List<ChatText> messages = base.getMessages();
		adapter = new ListAdapter<ChatText>(view.getContext(), messages, ChatBinder.ONLY);
		
		ListView list = (ListView)view.findViewById(R.id.chatroom_display_list);
		list.setAdapter(adapter);
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
				final ChatText choice = (ChatText)myAdapter.getItemAtPosition(myItemInt);
				
				if(choice.getActions().size() == 0) return;
				
				ChatActionDialog.create(choice).show(getFragmentManager(), "itemoptions");
			}
		});
		return view;
	}
	
	public void updateChannel(ChannelModel channel) {
		getArguments().putSerializable("base", channel);
		this.base = channel;
		
		if(adapter != null) {
			adapter.setElements(channel.getMessages());
		}
	}
	
	public ChannelModel getChannel() {
		if(this.base != null)
			return base;
		return (ChannelModel)getArguments().getSerializable("base");
	}
		
	public interface ChatroomHost {
		public void submitChatAction(ChatAction action, ChatText base);
	}
}
