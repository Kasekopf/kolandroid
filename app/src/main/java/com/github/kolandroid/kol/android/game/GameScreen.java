package com.github.kolandroid.kol.android.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.chat.newchat.ChatActivity;
import com.github.kolandroid.kol.android.chat.newchat.ChatConnection;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.controllers.NavigationController;
import com.github.kolandroid.kol.android.controllers.StatsController;
import com.github.kolandroid.kol.android.controllers.StatsController.StatsCallbacks;
import com.github.kolandroid.kol.android.screen.ActivityScreen;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.DrawerScreen;
import com.github.kolandroid.kol.android.screen.FragmentScreen;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.screen.ViewScreen;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.android.view.ProgressLoader;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.DataContext;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.models.NavigationModel;
import com.github.kolandroid.kol.model.models.StatsModel;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Logger;

public class GameScreen extends ActionBarActivity implements StatsCallbacks,
        ViewContext {
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

        final Screen baseScreen = new ActivityScreen(this);
        this.baseContext = new AndroidViewContext(this);

        View base = this.findViewById(R.id.game_progress_popup);
        ProgressBar bar = (ProgressBar) this
                .findViewById(R.id.game_progress_bar);
        TextView text = (TextView) this.findViewById(R.id.game_progress_text);

        this.loader = new ProgressLoader(base, bar, text);
        base.setVisibility(View.GONE);

        mTitle = getTitle();

        Intent intent = this.getIntent();

        @SuppressWarnings("unchecked")
        ModelController<? extends Model> c = (ModelController<? extends Model>) intent
                .getSerializableExtra("controller");
        Model model = c.getModel();
        Session session = model.getSession();
        Log.i("GameScreen", "Session: " + session);

        // Set up the drawer.
        Controller nav = new NavigationController(new NavigationModel(session));
        DrawerScreen drawer = DrawerScreen.create(nav);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_drawer, drawer).commit();
        drawer.setUp(this, R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        this.stats = new StatsController(new StatsModel(session));
        ViewScreen statsScreen = (ViewScreen) findViewById(R.id.game_statsscreen);
        statsScreen.display(stats, baseScreen);

        chat = ChatConnection.create(this.getClass().getSimpleName());
        chat.connect(this);
        displayIntent(this.getIntent(), false);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
        displayIntent(intent, true);
    }

    private void displayIntent(Intent intent, final boolean addToBackStack) {
        Fragment dialog = getSupportFragmentManager().findFragmentByTag("dialog");
        if (dialog != null && dialog instanceof DialogFragment) {
            Logger.log("GameScreen", "Dismissing dialog box");
            ((DialogFragment) dialog).dismiss();
        }

        if (!intent.hasExtra("controller")) {
            // the intent to launch this came from a back action
            // i.e. back from the chat
            // Do not shift the game view
            Logger.log("GameScreen", "GameScreen received intent without controller");
            return;
        }

        final Controller controller = (Controller) intent
                .getSerializableExtra("controller");
        controller.chooseScreen(new ScreenSelection() {
            @Override
            public void displayExternal(Controller c) {
                Logger.log("GameScreen", "ERROR: Controller " + c + " has chosen to appear on an external screen. Rerouting...");
                displayPrimary(c);
            }

            @Override
            public void displayPrimary(Controller c) {
                Logger.log("GameScreen", "Displaying " + c + " on primary pane");
                FragmentScreen screen = FragmentScreen.create(controller);

                FragmentTransaction trans = getSupportFragmentManager()
                        .beginTransaction().replace(R.id.game_mainfragment, screen);
                if (addToBackStack)
                    trans = trans.addToBackStack(null);
                trans.commit();
                refreshStatsPane();
            }

            @Override
            public void displayDialog(Controller c) {
                Logger.log("GameScreen", "Displaying " + c + " on new dialog box");
                DialogScreen.display(c, new ActivityScreen(GameScreen.this));
            }
        });
    }

    public void refreshStatsPane() {
        if (stats != null)
            stats.refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chat.close(this);
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
                if (chat == null) {
                    Logger.log("GameScreen", "Unable to load chat model; not connected to AndroidBinder");
                    return false;
                }
                ChatModel model = chat.getModel();
                if (model == null) {
                    Logger.log("GameScreen", "Unable to load chat model; AndroidBinder [" + chat + "] has null reference");
                    return false;
                }

                Intent intent = new Intent(this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

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
    public void displayMessage(String message) {
        baseContext.displayMessage(message);
    }

    @Override
    public void register(StatsController statController) {
        this.stats = statController;
    }
}
