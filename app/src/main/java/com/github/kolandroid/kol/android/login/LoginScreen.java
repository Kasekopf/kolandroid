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
import com.github.kolandroid.kol.android.screen.FragmentScreen;
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
            LoginController login = new LoginController();
            FragmentScreen frag = FragmentScreen.create(login);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.game_mainfragment, frag).commit();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Kingdom of Loathing");

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
}
