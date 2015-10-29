package com.github.kolandroid.kol.android.game;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.controller.UpdatableController;
import com.github.kolandroid.kol.android.controller.UpdateController;
import com.github.kolandroid.kol.android.controllers.chat.ChatCounterController;
import com.github.kolandroid.kol.android.controllers.stats.StatsGlanceController;
import com.github.kolandroid.kol.android.controllers.web.WebController;
import com.github.kolandroid.kol.android.screen.ActivityScreen;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.DrawerScreen;
import com.github.kolandroid.kol.android.screen.FragmentScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.screen.ViewScreen;
import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.android.view.PopupLoader;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.models.stats.StatsGlanceModel;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Logger;

public class GameScreen extends ActivityScreen {
    private StatsGlanceController stats;
    private SidebarController sidebar;
    private DrawerScreen navigationScreen;

    private HandlerCallback<Void> sidebarUpdater;

    private LoadingContext loader;

    private Controller chatIconController;

    @Override
    protected AndroidViewContext createViewContext() {
        // Set up the bottom loading bar
        View base = this.findViewById(R.id.game_progress_popup);
        ProgressBar bar = (ProgressBar) this
                .findViewById(R.id.game_progress_bar);
        TextView text = (TextView) this.findViewById(R.id.game_progress_text);
        base.setVisibility(View.GONE);
        PopupLoader loader = new PopupLoader(base, bar, text);
        return new AndroidViewContext(this, loader, this.getClass());
    }

    @Override
    public Controller setup(Bundle savedInstanceState, Controller controller) {
        Session session = null;
        if (controller != null && controller instanceof ModelController) {
            // Load the session for the provided model
            @SuppressWarnings("unchecked")
            ModelController<? extends Model> c = (ModelController<? extends Model>) controller;
            Model model = c.getModel();
            session = model.getSession();
        }

        // Set up the chat icon.
        if (savedInstanceState != null && savedInstanceState.containsKey("chat_controller")) {
            chatIconController = (ChatCounterController) savedInstanceState.getSerializable("chat_controller");
        } else if (session != null) {
            chatIconController = new ChatCounterController(session);
        } else {
            Logger.log("GameScreen", "Controller " + controller + " has no session; cannot be used to start GameScreen");
        }

        // Set up the drawer.
        if (savedInstanceState != null && savedInstanceState.containsKey("sidebar_controller")) {
            sidebar = (SidebarController) savedInstanceState.getSerializable("sidebar_controller");
        } else if (session != null) {
            sidebar = new SidebarController(session);
        } else {
            Logger.log("GameScreen", "Controller " + controller + " has no session; cannot be used to start GameScreen");
        }

        // Set up the stats pane.
        if (savedInstanceState != null && savedInstanceState.containsKey("stats_controller")) {
            stats = (StatsGlanceController) savedInstanceState.get("stats_controller");
        } else if (session != null) {
            stats = new StatsGlanceController(new StatsGlanceModel(session));
        } else {
            Logger.log("GameScreen", "Controller " + controller + " has no session; cannot be used to start GameScreen");
        }

        if (stats != null && sidebar != null) {
            sidebarUpdater = new UpdateSidebarHandler(sidebar);
            stats.attachNotificationCallback(sidebarUpdater.weak());
        }

        if (sidebar != null) {
            navigationScreen = DrawerScreen.create(sidebar);
            getFragmentManager().beginTransaction()
                    .replace(R.id.navigation_drawer, navigationScreen).commit();
            navigationScreen.setUp(this, R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout), R.drawable.ic_map_black_24dp);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && stats != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            ViewScreen statsScreen = new ViewScreen(this);
            statsScreen.display(stats, this);

            //Set the custom view to fill the action bar
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(statsScreen, params);
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
            public void displayPrimaryUpdate(UpdateController c, boolean displayIfUnable) {
                // We have to do a more complicated check to see if the same controller type is currently displayed
                Fragment current = getFragmentManager().findFragmentByTag("game_screen");
                if (current != null && current instanceof FragmentScreen) {
                    Controller currentController = ((FragmentScreen) current).getController();
                    if (currentController != null && currentController instanceof UpdatableController) {
                        UpdatableController toUpdate = (UpdatableController) currentController;

                        if (toUpdate.tryApply(c.getUpdateType(), c.getModel())) {
                            Logger.log("GameScreen", "Update " + c + " applied to " + toUpdate);
                            refreshStatsPane();
                            return;
                        }
                        Logger.log("GameScreen", "Unable to apply update " + c + " to " + toUpdate);
                    }
                }

                if (displayIfUnable) {
                    displayPrimary(c);
                }
            }

            @Override
            public void displayChat(Controller c) {
                Logger.log("GameScreen", "ERROR: Controller " + c + " has chosen to appear on in the chat. Ignoring.");
            }

            private void displayPrimary(Controller c, boolean addToBackStack) {
                FragmentScreen screen = FragmentScreen.create(c);

                @SuppressLint("CommitTransaction") FragmentTransaction trans = getFragmentManager()
                        .beginTransaction().replace(R.id.game_main_screen, screen, "game_screen");

                if (addToBackStack) {
                    Logger.log("GameScreen", "History saved");
                    trans = trans.addToBackStack("a");
                }

                trans.commit();
                if (!(c instanceof WebController)) {
                    refreshStatsPane();
                }
            }

            @Override
            public void displayPrimary(Controller c) {
                Logger.log("GameScreen", "Displaying " + c + " on primary pane");
                displayPrimary(c, addToBackStack);
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
        return R.layout.activity_game_view;
    }

    public void refreshStatsPane() {
        if (stats != null)
            stats.refresh();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable("stats_controller", stats);
        savedInstanceState.putSerializable("sidebar_controller", sidebar);
        savedInstanceState.putSerializable("chat_controller", chatIconController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_screen, menu);
        View chatIconView = menu.findItem(R.id.action_chat).getActionView();

        if (chatIconController != null) {
            chatIconController.attach(chatIconView, this);
            chatIconController.connect(chatIconView, this);
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatIconController != null) {
            chatIconController.disconnect(this);
        }

        if (sidebarUpdater != null) {
            sidebarUpdater.close();
            sidebarUpdater = null;
        }
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
}
