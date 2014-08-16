package com.starfish.kol.android.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.chat.ChatConnection;
import com.starfish.kol.android.controller.Controller;
import com.starfish.kol.android.controller.ModelController;
import com.starfish.kol.android.controller.StatsController;
import com.starfish.kol.android.controller.StatsController.StatsCallbacks;
import com.starfish.kol.android.game.fragments.NavigationFragment;
import com.starfish.kol.android.screen.FragmentScreen;
import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.android.view.ProgressLoader;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.DataContext;
import com.starfish.kol.gamehandler.LoadingContext;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.models.StatsModel;
import com.starfish.kol.model.models.chat.ChatState;
import com.starfish.kol.request.ResponseHandler;

public class GameScreen extends ActionBarActivity implements StatsCallbacks, ViewContext {
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationFragment mNavigationDrawerFragment;

	private DialogFragment dialog;
	
	private StatsController stats;
	
	private ViewContext baseContext;
	private LoadingContext loader;
	
	private ChatConnection chat;
	
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_screen);
		
		this.baseContext = new AndroidViewContext(this);

		View base = (View)this.findViewById(R.id.game_progress_popup);
		ProgressBar bar = (ProgressBar)this.findViewById(R.id.game_progress_bar);
		TextView text = (TextView)this.findViewById(R.id.game_progress_text);
		
		this.loader = new ProgressLoader(base, bar, text);
		base.setVisibility(View.GONE);
		
		mNavigationDrawerFragment = (NavigationFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		
		mTitle = getTitle();

		Intent intent = this.getIntent();
		
		@SuppressWarnings("unchecked")
		ModelController<?, Model<?>> c = (ModelController<?, Model<?>>)intent.getSerializableExtra("controller");
		Model<?> model = c.getModel();
		Session session = model.getSession();
		Log.i("GameScreen", "Session: " + session);
		
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(session, R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		StatsController controller = new StatsController(new StatsModel(session));
		FragmentScreen screen = FragmentScreen.create(controller);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.game_statsfragment, screen).commit();

		chat = new ChatConnection(session) {
			@Override
			public void updateMessages(ChatState base) {
				//do nothing
			}
		};
		displayIntent(this.getIntent(), false);
	}

	@Override
	public void onNewIntent(Intent intent) {
		this.setIntent(intent);
		displayIntent(intent, true);
	}

	private void displayIntent(Intent intent, boolean addToBackStack) {
		if(dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	
		if(!intent.hasExtra("controller")) {
			//the intent to launch this came from a back action
			// i.e. back from the chat
			//Do not shift the game view
			Log.i("GameScreen", "GameScreen recieved intent without model");
			return;
		}
		
		Controller controller = (Controller)intent.getSerializableExtra("controller");
		FragmentScreen screen = FragmentScreen.create(controller);
		
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction()
				.replace(R.id.game_mainfragment, screen);
		if(addToBackStack)
			trans = trans.addToBackStack(null);
		trans.commit();
		
		if(stats != null)
			stats.refresh();
	}

	@Override
	public void onStop() {
		super.onStop();
		chat.stop(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		chat.start(this);
	}
	
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_testing, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_quests:
			if (stats != null)
				stats.showQuests();
			return true;
		case R.id.action_chat:
			if(chat != null)
				chat.openChat(this, this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void update(String username, String subtext) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(username);
		actionBar.setSubtitle(subtext);
	}
	
	@Override
	public ResponseHandler getPrimaryRoute() {
		return baseContext.getPrimaryRoute();
	}
	
	@Override
	public LoadingContext createLoadingContext() {
		return loader;
	}

	@Override
	public DataContext getDataContext() {
		return baseContext.getDataContext();
	}

	@Override
	public void register(StatsController statController) {
		this.stats = statController;
	}
}
