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

import com.starfish.kol.android.R;
import com.starfish.kol.android.chat.ChatService;
import com.starfish.kol.android.chat.ChatService.ChatCallback;
import com.starfish.kol.android.dialogs.WebDialog;
import com.starfish.kol.android.game.GameFragment.GameCallbacks;
import com.starfish.kol.android.game.fragments.ChoiceFragment;
import com.starfish.kol.android.game.fragments.CraftingFragment;
import com.starfish.kol.android.game.fragments.FightFragment;
import com.starfish.kol.android.game.fragments.InventoryFragment;
import com.starfish.kol.android.game.fragments.NavigationFragment;
import com.starfish.kol.android.game.fragments.SkillsFragment;
import com.starfish.kol.android.game.fragments.StatsFragment;
import com.starfish.kol.android.game.fragments.StatsFragment.StatsCallbacks;
import com.starfish.kol.android.game.fragments.WebFragment;
import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.models.ChoiceModel;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.FightModel;
import com.starfish.kol.model.models.InventoryModel;
import com.starfish.kol.model.models.SkillsModel;
import com.starfish.kol.model.models.StatsModel;
import com.starfish.kol.model.models.WebModel;

public class GameScreen extends ActionBarActivity implements StatsCallbacks,
		GameCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationFragment mNavigationDrawerFragment;

	private DialogFragment dialog;
	
	private StatsFragment mStatsFragment;
	private GameFragment mainFragment;
	
	private ChatCallback chat;
	
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_screen);
		
		mNavigationDrawerFragment = (NavigationFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		
		mTitle = getTitle();

		Intent intent = this.getIntent();
		Model<?> model = (Model<?>)intent.getSerializableExtra("model");
		Session session = model.getSession();
		Log.i("GameScreen", "Session: " + session);
		
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(session, R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		mStatsFragment = new StatsFragment();
		mStatsFragment.setArguments(GameFragment.getModelBundle(new StatsModel(session)));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.game_statsfragment, mStatsFragment).commit();

		chat = new ChatCallback() {
			@Override
			public void updateMessages(ChatService base) {
				//do nothing
			}			
		};
        
		chat.open(session, this);
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
		
		if(!intent.hasExtra("modeltype")) {
			//the intent to launch this came from a back action
			// i.e. back from the chat
			//Do not shift the game view
			return;
		}

		Bundle bundle = new Bundle();
		bundle.putSerializable("model", intent.getSerializableExtra("model"));
		bundle.putSerializable("modeltype", intent.getSerializableExtra("modeltype"));
		
		Class<?> type = (Class<?>) intent.getSerializableExtra("modeltype");
		
		GameFragment frag = null;
		
		if (type == WebModel.class) {
			WebModel model = (WebModel)intent.getSerializableExtra("model");
			if(model.isSmall()) {
				dialog = new WebDialog();
				dialog.setArguments(bundle);
			    dialog.show(getSupportFragmentManager(), "dialog");
			    return;
			}
			frag = new WebFragment<WebModel>();
		} else if (type == FightModel.class) {
			frag = new FightFragment();
		} else if (type == ChoiceModel.class) {
			frag = new ChoiceFragment();
		} else if (type == InventoryModel.class) {
			frag = new InventoryFragment();
		} else if (type == SkillsModel.class) {
			frag = new SkillsFragment();
		} else if (type == CraftingModel.class) {
			frag = new CraftingFragment();
		} else {
			Log.e("FragmentStack", "Unable to match view to model " + type);
			return;
		}
		frag.setArguments(bundle);
				
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction()
				.replace(R.id.game_mainfragment, frag);
		if(addToBackStack)
			trans = trans.addToBackStack(null);
		trans.commit();

		mainFragment = frag;
		mStatsFragment.refresh();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		chat.close(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		chat.refresh();
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
		case R.id.menu_checkweb:
			if (mainFragment != null)
				mainFragment.getModel().simulateWebRequest();
			return true;
		case R.id.action_quests:
			if (mStatsFragment != null)
				mStatsFragment.showQuests();
			return true;
		case R.id.action_chat:
			if(chat.getService() != null)
				chat.getService().openChat(new AndroidViewContext(this));
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
	public void refreshStats() {
		mStatsFragment.refresh();
	}
}
