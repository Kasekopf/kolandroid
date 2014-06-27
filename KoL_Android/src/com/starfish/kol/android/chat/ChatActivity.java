package com.starfish.kol.android.chat;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost.OnTabChangeListener;

import com.starfish.kol.android.R;
import com.starfish.kol.android.chat.ChatService.ChatCallback;
import com.starfish.kol.android.chat.ChatroomFragment.ChatroomHost;
import com.starfish.kol.android.util.AndroidProgressHandler;
import com.starfish.kol.android.util.CustomFragmentTabHost;
import com.starfish.kol.android.util.CustomFragmentTabHost.TabInfo;
import com.starfish.kol.model.ProgressHandler;
import com.starfish.kol.model.models.chat.ChatAction;
import com.starfish.kol.model.models.chat.ChatAction.ChatActionSubmission;
import com.starfish.kol.model.models.chat.ChatChannel;
import com.starfish.kol.model.models.chat.ChatText;

public class ChatActivity extends ActionBarActivity implements
		ChatroomHost {
	private ChatCallback chat;
	private EditText text;
	private CustomFragmentTabHost host;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_chat_screen);
		overridePendingTransition(R.anim.inleftanim, R.anim.outleftanim);

		chat = new ChatCallback() {
			@Override
			public void updateMessages(ChatService base) {
				ChatActivity.this.updateMessages(base);
			}
		};
		
		chat.open(this);
		
		host = (CustomFragmentTabHost) findViewById(R.id.tabs_tabhost);

		host.setup(this, getSupportFragmentManager());
		// R.id.tabs_tabcontent);
		host.clearAllTabs();
		host.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabid) {
				System.out.println("Tab changed to " + tabid);
				if(chat != null && chat.getService() != null)
					chat.getService().setCurrentRoom(tabid);
			}
			
		});

		text = (EditText) findViewById(R.id.chatroom_text_input);
		

		Button submit = (Button)findViewById(R.id.chatroom_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String msg = text.getText().toString();
				String channel = host.getCurrentTabTag();
				if(chat != null && chat.getService() != null) {
					chat.getService().submitChat(channel, msg);
					text.setText("");
				}
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}			
		});
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			overridePendingTransition(R.anim.inrightanim, R.anim.outrightanim);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onStart() {
		super.onStart();
		chat.refresh();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		chat.close(this);
	}

	public void updateMessages(ChatService base) {		
		ArrayList<ChatChannel> channels = base.getChannels();
		for (ChatChannel channel : channels) {
			String tag = channel.getName();
			TabInfo ti = host.getChildByTag(tag);
			
			if (ti == null) {
				if(!channel.isActive()) {
					//Do not make a tab for an inactive channel.
					continue;
				}
				Log.i("ChatService", "Making new tab for '" + tag + "'");
				Bundle bund = new Bundle();
				bund.putSerializable("list", channel.getMessages());
				host.addTab(host.newTabSpec(tag).setIndicator(tag),
						ChatroomFragment.class, bund);
			} else if (ti.getFragment() == null) {
				// Tab exists, but fragment has not been created.
				// update the list of messages in the arguments.
				ti.getArgs().putSerializable("list", channel.getMessages());
			} else {
				ChatroomFragment room = (ChatroomFragment) ti.getFragment();
				room.setList(channel.getMessages());
			}
		}
	}

	@Override
	public void submitChatAction(ChatAction action, ChatText baseMessage) {
		ProgressHandler<String> callback = new AndroidProgressHandler<String>(){
			@Override
			public void recieveProgress(String message) {
				text.setText(message);
			}
		};
		
		ChatActionSubmission sub = action.getPartialSubmission(baseMessage, callback);
		if(chat.getService() != null)
			chat.getService().submitChatAction(sub);
	}
}
