package com.starfish.kol.android.chat;

import java.util.ArrayList;
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
import com.starfish.kol.android.dialogs.ChatDialog;
import com.starfish.kol.android.util.adapters.ListAdapter;
import com.starfish.kol.android.util.listbuilders.ChatBuilder;
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.model.models.chat.ChatAction;
import com.starfish.kol.model.models.chat.ChatText;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Use the
 * {@link ChatroomFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class ChatroomFragment extends Fragment {
	private ListAdapter<ChatText> adapter;
	
	public ChatroomFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

		@SuppressWarnings("unchecked")
		List<ChatText> messages = (List<ChatText>)getArguments().getSerializable("list");
		adapter = new ListAdapter<ChatText>(view.getContext(), messages, new ChatBuilder());
		
		ListView list = (ListView)view.findViewById(R.id.chatroom_display_list);
		list.setAdapter(adapter);
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
				final ChatText choice = (ChatText)myAdapter.getItemAtPosition(myItemInt);
				
				if(choice.getActions().size() == 0) return;
				
				ChatDialog dialog = ChatDialog.create(choice);
				dialog.setOnSelection(new OnListSelection<ChatAction>() {
					@Override
					public boolean selectItem(ChatAction item) {
						((ChatroomHost)getActivity()).submitChatAction(item, choice);
						return true;
					}
				});
				dialog.show(getFragmentManager(), "itemoptions");
			}
		});
		return view;
	}
	
	public void setList(ArrayList<ChatText> messages) {
		getArguments().putSerializable("list", messages);
		if(adapter != null) {
			adapter.setElements(messages);
			//Log.i("ChatroomFragment", "Updating message list");
		} else {
			//Log.i("ChatroomFragment", "Adapter was null");
		}
	}
		
	public interface ChatroomHost {
		public void submitChatAction(ChatAction action, ChatText base);
	}
}
