package com.starfish.kol.android.chat;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.chat.ChatroomFragment.ChatroomHost;
import com.starfish.kol.android.util.adapters.ListAdapter;
import com.starfish.kol.android.util.listbuilders.TextBuilder;
import com.starfish.kol.model.models.chat.ChatAction;
import com.starfish.kol.model.models.chat.ChatText;

public class ChatActionDialog extends DialogFragment {
	public static ChatActionDialog create(ChatText base) {
		ChatActionDialog dialog = new ChatActionDialog();
		Bundle args = new Bundle();
		
		args.putSerializable("base", base);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		ChatText base = (ChatText)this.getArguments().getSerializable("base");
		dialog.setTitle(base.getTitle());
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = (View)inflater.inflate(R.layout.dialog_chat_screen,
				container, false);

		final ChatText base = (ChatText)this.getArguments().getSerializable("base");

	    ListAdapter<ChatAction> adapter = new ListAdapter<ChatAction>(this.getActivity(), base.getActions(), new TextBuilder<ChatAction>());
	    
	    ListView list = (ListView)rootView.findViewById(R.id.dialog_chat_list);
	    list.setAdapter(adapter);
	    list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> ad, View list, int pos,
					long arg3) {
				ChatAction select = (ChatAction)ad.getItemAtPosition(pos);

				if(select != null) {
					ChatroomHost host = (ChatroomHost)getActivity();
					host.submitChatAction(select, base);
					ChatActionDialog.this.dismiss();
				}
			}
	    });
		
		return rootView;
	}
}
