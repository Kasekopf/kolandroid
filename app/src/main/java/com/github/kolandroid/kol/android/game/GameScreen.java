package com.github.kolandroid.kol.android.game;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.controllers.NavigationController;
import com.github.kolandroid.kol.android.controllers.StatsController;
import com.github.kolandroid.kol.android.controllers.StatsController.StatsCallbacks;
import com.github.kolandroid.kol.android.controllers.chat.ChatCounterController;
import com.github.kolandroid.kol.android.controllers.web.WebController;
import com.github.kolandroid.kol.android.screen.ActivityScreen;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.DrawerScreen;
import com.github.kolandroid.kol.android.screen.FragmentScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.screen.ViewScreen;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.android.view.ProgressLoader;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.models.NavigationModel;
import com.github.kolandroid.kol.model.models.StatsModel;
import com.github.kolandroid.kol.util.Logger;

public class GameScreen extends ActivityScreen implements StatsCallbacks {
    private StatsController stats;
    private DrawerScreen navigationScreen;

    private LoadingContext loader;

    private Controller chatIconController;
    private View chatIconView;

    @Override
    protected AndroidViewContext createViewContext() {
        // Set up the bottom loading bar
        View base = this.findViewById(R.id.game_progress_popup);
        ProgressBar bar = (ProgressBar) this
                .findViewById(R.id.game_progress_bar);
        TextView text = (TextView) this.findViewById(R.id.game_progress_text);
        base.setVisibility(View.GONE);
        ProgressLoader loader = new ProgressLoader(base, bar, text);
        return new AndroidViewContext(this, loader, this.getClass());
    }

    @Override
    public Controller setup(Bundle savedInstanceState, Controller controller) {
        if (controller != null && controller instanceof ModelController) {
            // Load the session for the provided model
            @SuppressWarnings("unchecked")
            ModelController<? extends Model> c = (ModelController<? extends Model>) controller;
            Model model = c.getModel();
            Session session = model.getSession();

            chatIconController = new ChatCounterController(session);

            // Set up the drawer.
            Controller nav = new NavigationController(new NavigationModel(session));
            navigationScreen = DrawerScreen.create(nav);
            getFragmentManager().beginTransaction()
                    .replace(R.id.navigation_drawer, navigationScreen).commit();
            navigationScreen.setUp(this, R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));

            // Set up the stats pane.
            this.stats = new StatsController(new StatsModel(session));
            ViewScreen statsScreen = (ViewScreen) findViewById(R.id.game_statsscreen);
            statsScreen.display(stats, this);
        } else {
            Logger.log("GameScreen", "Controller " + controller + " has no session; cannot be used to start GameScreen");
        }

        return controller;
    }

    @Override
    protected void displayController(Controller c, final boolean addToBackStack) {
        Fragment dialog = getFragmentManager().findFragmentByTag("dialog");
        if (dialog != null && dialog instanceof DialogFragment) {
            Logger.log("GameScreen", "Dismissing dialog box");
            ((DialogFragment) dialog).dismiss();
        }

        c.chooseScreen(new ScreenSelection() {
            @Override
            public void displayExternal(Controller c) {
                Logger.log("GameScreen", "ERROR: Controller " + c + " has chosen to appear on an external screen. Rerouting...");
                displayPrimary(c, false);
            }

            @Override
            public void displayExternalDialog(Controller c, boolean cancellable) {
                Logger.log("GameScreen", "ERROR: Controller " + c + " has chosen to appear on an external dialog. Rerouting...");
                displayDialog(c);
            }

            @Override
            public void displayChat(Controller c) {
                Logger.log("GameScreen", "ERROR: Controller " + c + " has chosen to appear on in the chat. Ignoring.");
            }

            @Override
            public void displayPrimary(Controller c, boolean replaceSameType) {
                Logger.log("GameScreen", "Displaying " + c + " on primary pane [replace=" + replaceSameType + "]");
                FragmentScreen screen = FragmentScreen.create(c);

                @SuppressLint("CommitTransaction") FragmentTransaction trans = getFragmentManager()
                        .beginTransaction().replace(R.id.game_mainfragment, screen, "gamescreen");

                boolean doAddToBackStack = addToBackStack;
                if (doAddToBackStack && replaceSameType) {
                    // We have to do a more complicated check to see if the same controller type is currently displayed
                    Fragment current = getFragmentManager().findFragmentByTag("gamescreen");
                    if (current != null && current instanceof FragmentScreen) {
                        Controller currentController = ((FragmentScreen) current).getController();
                        if (currentController != null && c.getClass() == currentController.getClass()) {
                            Logger.log("GameScreen", "<" + c.getClass() + "> already on screen");
                            doAddToBackStack = false;
                        }
                    }
                }

                if (doAddToBackStack) {
                    Logger.log("GameScreen", "History saved");
                    trans = trans.addToBackStack("a");
                }

                trans.commit();
                if (!(c instanceof WebController)) {
                    refreshStatsPane();
                }
            }

            @Override
            public void displayDialog(Controller c) {
                Logger.log("GameScreen", "Displaying " + c + " on new dialog box");
                DialogScreen.display(c, GameScreen.this);
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_game_screen;
    }

    public void refreshStatsPane() {
        if (stats != null)
            stats.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_screen, menu);
        View chatIconView = menu.findItem(R.id.action_chat).getActionView();
        chatIconController.connect(chatIconView, this);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatIconController.disconnect(this);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //This appears to be necessary to make fragment backtracking actually work without the v4 support library...

        // Close the navigation drawer if open
        if (navigationScreen != null && navigationScreen.isOpen()) {
            navigationScreen.close();
            return;
        }

        // Attempt to pop the next fragment off the stack
        if (!getFragmentManager().popBackStackImmediate()) {
            //TODO: replace with Logout? message
            super.onBackPressed();
        }
    }

    @Override
    public void update(String username, String subtext) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(username);
            actionBar.setSubtitle(subtext);
        }
    }

    @Override
    public void register(StatsController statController) {
        this.stats = statController;
    }
}
