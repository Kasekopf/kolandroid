package com.starfish.kol.android.chat;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

import com.starfish.kol.android.R;
import com.starfish.kol.android.chat.ChatChannelDialog.ChatChannelDialogCallback;
import com.starfish.kol.android.chat.ChatService.ChatCallback;
import com.starfish.kol.android.chat.ChatroomFragment.ChatroomHost;
import com.starfish.kol.android.util.AndroidProgressHandler;
import com.starfish.kol.android.util.CustomFragmentTabHost;
import com.starfish.kol.android.util.CustomFragmentTabHost.TabInfo;
import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.ProgressHandler;
import com.starfish.kol.model.elements.interfaces.DeferredAction;
import com.starfish.kol.model.models.chat.ChatAction;
import com.starfish.kol.model.models.chat.ChatAction.ChatActionSubmission;
import com.starfish.kol.model.models.chat.ChatChannel;
import com.starfish.kol.model.models.chat.ChatModel;
import com.starfish.kol.model.models.chat.ChatText;

public class ChatActivity extends ActionBarActivity implements
		ChatroomHost, ChatChannelDialogCallback, ViewContext {
	private ChatCallback chat;
	private EditText text;
	private CustomFragmentTabHost host;
	
	private ChatChannelDialog dialog = null;
	private ViewContext baseContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_chat_screen);
		overridePendingTransition(R.anim.inleftanim, R.anim.outleftanim);

		this.baseContext = new AndroidViewContext(this);
		
		Session session = (Session)this.getIntent().getSerializableExtra("session");
		
		chat = new ChatCallback() {
			@Override
			public void updateMessages(ChatService base) {
				ChatActivity.this.updateMessages(base);
			}
		};
		
		chat.open(session, this);
		
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
		case R.id.action_channels:
			if(chat.getService() != null) {
				dialog = ChatChannelDialog.create(chat.getService().getChannels());
				dialog.show(getSupportFragmentManager(), "channeldialog");
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
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

	private void addTab(final ChatChannel base) {
		String tag = base.getName();
		
		Log.i("ChatService", "Making new tab for '" + tag + "'");
		Bundle bund = new Bundle();
		bund.putSerializable("base", base);
		host.addTab(host.newTabSpec(tag).setIndicator(tag),
				ChatroomFragment.class, bund);
		
		View tabTitle = host.getTabByTag(tag);
		tabTitle.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				new AlertDialog.Builder(ChatActivity.this)
		        .setTitle("Close " + base.getName() + "?")
		        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
						if(chat.getService() != null) {
							chat.getService().submitChatAction(base.leave());
						}
		            	dialog.dismiss();		            	
		            }

		        })
		        .setNegativeButton("Cancel", null)
		        .show();
				return true;
			}
		});
	}
	
	public void updateMessages(ChatService base) {		
		ArrayList<ChatChannel> channels = base.getChannels();
		if(dialog != null)
			dialog.updateChannels(channels);
		
		for (ChatChannel channel : channels) {
			String tag = channel.getName();
			TabInfo ti = host.getChildByTag(tag);
			
			if (ti == null) {
				if(!channel.isActive()) {
					//Do not make a tab for an inactive channel.
					continue;
				}
				
				this.addTab(channel);				
			} else if (ti.getFragment() == null) {
				// Tab exists, but fragment has not been created.
				// update the list of messages in the arguments.
				ti.getArgs().putSerializable("base", channel);
			} else {
				ChatroomFragment room = (ChatroomFragment) ti.getFragment();
				room.updateChannel(channel);
			}
		}
		
		ArrayList<TabInfo> tabs = host.getTabs();
		TabWidget tabViews = host.getTabWidget();
		ChatChannel channel;
		
		boolean currentRemoved = false;
		
		for(int index = 0; index < tabs.size(); index++) {
			TabInfo tab = tabs.get(index);
			
			if(tab.getFragment() == null) {
				channel = (ChatChannel)tab.getArgs().getSerializable("base");
			} else {
				ChatroomFragment room = (ChatroomFragment) tab.getFragment();
				channel = room.getChannel();
			}
			
			int visibility = channel.isActive() ? View.VISIBLE : View.GONE;
			tabViews.getChildTabViewAt(index).setVisibility(visibility);
			
			if(!channel.isActive() && index == host.getCurrentTab()) {
				currentRemoved = true;
			}
		}
		
		//If the current tab was removed, transition to a nearby tab
		if(currentRemoved) {
			for(int j = host.getCurrentTab() + 1; j < tabs.size(); j++) {
				if(tabViews.getChildTabViewAt(j).getVisibility() == View.VISIBLE) {
					host.setCurrentTab(j);
					currentRemoved = false;
					break;
				}
			}
			
			if(currentRemoved) {
				for(int j = host.getCurrentTab() - 1; j >= 0; j--) {
					if(tabViews.getChildTabViewAt(j).getVisibility() == View.VISIBLE) {
						host.setCurrentTab(j);
						break;
					}
				}
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

	@Override
	public void onChannelAction(DeferredAction<ChatModel> action) {
		if(chat.getService() != null) {
			chat.getService().submitChatAction(action);
		}
	}

	@Override
	public void onChannelSelect(ChatChannel channel) {
		if(channel.isActive())
			host.setCurrentTabByTag(channel.getName());
	}

	@Override
	public <E extends Model<?>> void display(E model) {
		baseContext.display(model);
	}
}
