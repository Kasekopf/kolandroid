package com.github.kolandroid.kol.android.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.chat.ChatService;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.ActivityScreen;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.FragmentScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.gamehandler.DataContext;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Logger;

public class LoginScreen extends ActionBarActivity implements ViewContext {
    private ViewContext baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Logger.override_logger(new Logger() {
            @Override
            public void do_log(String tag, String message) {
                Log.v(tag, message);
            }
        });

        this.baseContext = new AndroidViewContext(this);

        if (savedInstanceState == null) {
            displayController(new LoginController());
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Kingdom of Loathing");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        //https://code.google.com/p/android/issues/detail?id=19917
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);

        if (!intent.hasExtra("controller")) {
            // the intent to launch this came from a back action
            // i.e. back from the chat
            // Do not shift the game view
            Logger.log("GameScreen", "LoginScreen received intent without controller");
            return;
        }

        final Controller controller = (Controller) intent
                .getSerializableExtra("controller");
        displayController(controller);
    }

    private void displayController(Controller controller) {
        controller.chooseScreen(new ScreenSelection() {
            @Override
            public void displayExternal(Controller c) {
                Logger.log("LoginScreen", "Displaying " + c + " on external pane");
                FragmentScreen frag = FragmentScreen.create(c);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.login_mainfragment, frag, "loginscreen").commit();
            }

            @Override
            public void displayExternalDialog(Controller c) {
                Logger.log("GameScreen", "Displaying " + c + " on new dialog box.");
                DialogScreen.display(c, new ActivityScreen(LoginScreen.this));
                displayDialog(c);
            }

            @Override
            public void displayPrimary(Controller c, boolean replaceSameType) {
                Logger.log("LoginScreen", "ERROR: Controller " + c + " has chosen to appear on a primary screen. Ignoring.");
            }

            @Override
            public void displayDialog(Controller c) {
                Logger.log("LoginScreen", "ERROR: Controller " + c + " has chosen to appear on a primary dialog. Ignoring.");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        stopService(new Intent(this, ChatService.class));
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

    @Override
    public ResponseHandler getPrimaryRoute() {
        return baseContext.getPrimaryRoute();
    }

    @Override
    public LoadingContext createLoadingContext() {
        return LoadingContext.NONE;
    }

    @Override
    public DataContext getDataContext() {
        return baseContext.getDataContext();
    }

    @Override
    public void displayMessage(String message) {
        baseContext.displayMessage(message);
    }
}
