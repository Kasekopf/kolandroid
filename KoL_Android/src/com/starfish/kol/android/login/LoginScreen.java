package com.starfish.kol.android.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.starfish.kol.android.R;
import com.starfish.kol.android.chat.ChatService;
import com.starfish.kol.android.view.ModelWrapper;
import com.starfish.kol.model.models.login.LoginModel;

public class LoginScreen extends ActionBarActivity {	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);

		
		if (savedInstanceState == null) {

			LoginModel login = new LoginModel();
			ModelWrapper wrapper = new ModelWrapper(login);
			
			LoginFragment frag = new LoginFragment();
			frag.setArguments(wrapper.toBundle());

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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
