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
import com.starfish.kol.android.dialogs.WebDialog;
import com.starfish.kol.android.game.fragments.ChoiceFragment;
import com.starfish.kol.android.game.fragments.CraftingFragment;
import com.starfish.kol.android.game.fragments.FightFragment;
import com.starfish.kol.android.game.fragments.NavigationFragment;
import com.starfish.kol.android.game.fragments.SkillsFragment;
import com.starfish.kol.android.game.fragments.StatsFragment;
import com.starfish.kol.android.game.fragments.StatsFragment.StatsCallbacks;
import com.starfish.kol.android.game.fragments.WebFragment;
import com.starfish.kol.android.game.fragments.inventory.InventoryFragment;
import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.android.view.ModelWrapper;
import com.starfish.kol.android.view.ProgressLoader;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.LoadingContext;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.models.ChoiceModel;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.FightModel;
import com.starfish.kol.model.models.StatsModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.chat.ChatState;
import com.starfish.kol.model.models.inventory.InventoryModel;
import com.starfish.kol.model.models.skill.SkillsModel;

public class GameScreen extends ActionBarActivity implements StatsCallbacks, ViewContext {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationFragment mNavigationDrawerFragment;

	private DialogFragment dialog;
	
	private StatsFragment mStatsFragment;
	
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
		ModelWrapper wrapper = new ModelWrapper(intent);		
		Model<?> model = wrapper.getModel();
		
		Session session = model.getSession();
		Log.i("GameScreen", "Session: " + session);
		
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(session, R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		mStatsFragment = new StatsFragment();
		mStatsFragment.setArguments(ModelWrapper.bundle(new StatsModel(session)));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.game_statsfragment, mStatsFragment).commit();

		chat = new ChatConnection(session, this) {
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
		
		ModelWrapper wrapper = new ModelWrapper(intent); //should this really be Void?
		if(!wrapper.hasModel()) {
			//the intent to launch this came from a back action
			// i.e. back from the chat
			//Do not shift the game view
			Log.i("GameScreen", "GameScreen recieved intent without model");
			return;
		}
		
		Bundle bundle = wrapper.toBundle();
		Class<?> type = wrapper.getModelType();

		Log.i("GameScreen", "GameScreen recieved intent with model " + type);
		
		GameFragment<?, ?> frag = null;
		
		if (type == WebModel.class) {
			WebModel model = (WebModel)wrapper.getModel();
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
		mStatsFragment.refresh();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		chat.close(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		chat.pause();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		chat.unpause();
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
			if (mStatsFragment != null)
				mStatsFragment.showQuests();
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
	public <E extends Model<?>> void display(E model) {
		baseContext.display(model);
	}

	@Override
	public LoadingContext createLoadingContext() {
		return loader;
	}
}
