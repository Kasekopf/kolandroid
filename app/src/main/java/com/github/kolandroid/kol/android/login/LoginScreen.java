package com.github.kolandroid.kol.android.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.chat.ChatBroadcaster;
import com.github.kolandroid.kol.android.chat.ChatService;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.ActivityScreen;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.FragmentScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.util.Logger;

public class LoginScreen extends ActivityScreen {
    private Controller current = null;

    private DialogScreen dialog = null;

    private final HandlerCallback<Void> closeDialog = new HandlerCallback<Void>() {
        @Override
        protected void receiveProgress(Void message) {
            if (dialog != null) {
                dialog.close();
                dialog = null;
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        //https://code.google.com/p/android/issues/detail?id=19917
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public Controller setup(Bundle savedInstanceState, Controller controller) {
        Logger.override_logger(new Logger() {
            @Override
            public void do_log(String tag, String message) {
                Log.v(tag, message);
            }
        });

        // Default to displaying the Login Screen on app startup
        if (controller == null) {
            controller = new LoginConnectingController();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("The Kingdom of Loathing");
        }

        startService(new Intent(this, ChatService.class));
        return controller;
    }

    @Override
    protected void displayController(Controller controller, boolean addToBackStack) {
        controller.chooseScreen(new ScreenSelection() {
            @Override
            public void displayExternal(Controller c) {
                Logger.log("LoginScreen", "Displaying " + c + " on external pane");
                current = c;

                FragmentScreen frag = FragmentScreen.create(c);
                getFragmentManager().beginTransaction() //.setCustomAnimations(R.animator.fadeinanimator, R.animator.fadeoutanimator)
                        .add(R.id.login_mainfragment, frag, "loginscreen").commit();

                closeDialog.execute(null, 500);
            }

            @Override
            public void displayExternalDialog(Controller c, boolean cancellable) {
                Logger.log("GameScreen", "Displaying " + c + " on new dialog box.");
                if (dialog != null) {
                    dialog.close();
                }

                dialog = DialogScreen.display(c, LoginScreen.this);
                dialog.setCancelable(cancellable);
            }

            @Override
            public void displayPrimary(Controller c, boolean replaceSameType) {
                Logger.log("LoginScreen", "ERROR: Controller " + c + " has chosen to appear on a primary screen. Ignoring.");
            }

            @Override
            public void displayDialog(Controller c) {
                displayExternalDialog(c, true);
            }

            @Override
            public void displayChat(Controller c) {
                Logger.log("LoginScreen", "ERROR: Controller " + c + " has chosen to appear on in the chat. Ignoring.");
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login_screen;
    }

    @Override
    public void onResume() {
        super.onResume();

        Logger.log("LoginScreen", "Resuming...");

        // Stop the chat if running
        ChatBroadcaster.sendCommand(this, ChatModel.ChatModelCommand.StopChat.ONLY);

        // If the currently displayed LoginModel is stale, we have to reload the login page
        if (current != null && current instanceof LoginController && ((LoginController) current).getModel().isStale()) {
            this.displayController(new LoginConnectingController(), false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return (id == R.id.action_settings) || super.onOptionsItemSelected(item);
    }
}
