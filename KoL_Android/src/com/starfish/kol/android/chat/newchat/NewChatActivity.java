package com.starfish.kol.android.chat.newchat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.starfish.kol.android.R;
import com.starfish.kol.android.chat.newchat.ChatActionsController.ChatActionsControllerHost;
import com.starfish.kol.android.chat.newchat.ChatSubmissionController.ChatSubmissionControllerHost;
import com.starfish.kol.android.screen.DialogScreen;
import com.starfish.kol.android.screen.FragmentScreen;
import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.gamehandler.DataContext;
import com.starfish.kol.gamehandler.LoadingContext;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.request.ResponseHandler;

public class NewChatActivity extends ActionBarActivity implements ViewContext,
		ChatActionsControllerHost, ChatSubmissionControllerHost {
	private ViewContext baseContext;
	private Screen baseScreen = new Screen() {
		@Override
		public FragmentManager getFragmentManager() {
			return NewChatActivity.this.getSupportFragmentManager();
		}

		@Override
		public Activity getActivity() {
			return NewChatActivity.this;
		}

		@Override
		public ViewContext getViewContext() {
			return NewChatActivity.this;
		}

		@Override
		public FragmentManager getChildFragmentManager() {
			return NewChatActivity.this.getSupportFragmentManager();
		}

		@Override
		public void close() {
			// do nothing...?
		}
	};

	private ChatController mainChat;
	private ChatSubmissionController chatSubmission;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_screen);

		this.baseContext = new AndroidViewContext(this);

		mainChat = new ChatController();
		FragmentScreen chatScreen = FragmentScreen.create(mainChat);
		FragmentTransaction trans = getSupportFragmentManager()
				.beginTransaction().replace(R.id.chat_chatscreen, chatScreen);
		trans.commit();
		
		chatSubmission = new ChatSubmissionController();
		FragmentScreen subScreen = FragmentScreen.create(chatSubmission);
		FragmentTransaction trans2 = getSupportFragmentManager()
				.beginTransaction().replace(R.id.chat_submissionscreen, subScreen);
		trans2.commit();

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
			ChatChannelsController controller = new ChatChannelsController();
			DialogScreen.display(controller, baseScreen);
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
	public ResponseHandler getPrimaryRoute() {
		return baseContext.getPrimaryRoute();
	}

	@Override
	public LoadingContext createLoadingContext() {
		return baseContext.createLoadingContext();
	}

	@Override
	public DataContext getDataContext() {
		return baseContext.getDataContext();
	}

	@Override
	public void fillChatText(String text) {
		chatSubmission.fillChatText(text);
	}

	@Override
	public String getCurrentChannel() {
		return mainChat.getCurrentChannel();
	}
}
