package com.starfish.kol.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.starfish.kol.android.chat.ChatService;
import com.starfish.kol.android.game.BaseGameFragment;
import com.starfish.kol.android.view.ApplicationView;
import com.starfish.kol.model.models.LoginModel;
import com.starfish.kol.model.models.LoginModel.LoginStatus;

public class LoginScreen extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);

		ApplicationView app = (ApplicationView)getApplication();
		LoginModel login = app.getStart();
		
		if (savedInstanceState == null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("model", login);
			LoginFragment frag = new LoginFragment();
			frag.setArguments(bundle);

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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class LoginFragment extends BaseGameFragment<LoginStatus, LoginModel> {
		public LoginFragment() {
			super(R.layout.fragment_login_screen);
		}

		@Override
		public void onCreateSetup(View view, LoginModel base,
				Bundle savedInstanceState) {

			final EditText user = (EditText) view
					.findViewById(R.id.login_username);
			final EditText pass = (EditText) view
					.findViewById(R.id.login_password);
			final Button login = (Button) view
					.findViewById(R.id.login_btnlogin);

			pass.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView view, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_GO) {
						login.performClick();
						return true;
					}
					return false;
				}

			});

			login.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//model.cheat();
					
					String username = user.getText().toString();
					String password = pass.getText().toString();
					if (username == null || password == null
							|| username.length() == 0 || password.length() == 0)
						return;

					InputMethodManager inputManager = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);

					inputManager.hideSoftInputFromWindow(getActivity()
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

					getModel().login(username, password);
				}
			});

		}

		@Override
		protected void recieveProgress(LoginStatus message) {
			switch (message) {
			case SUCCESS:
				Toast.makeText(getActivity(), "Logged in!!",
						Toast.LENGTH_SHORT).show();
				
				Context context = getActivity().getApplicationContext();
				Intent i = new Intent(context, ChatService.class);
				context.startService(i);
				
				break;
			case FAILED_ACCESS:
				Toast.makeText(getActivity(), "Failed access",
						Toast.LENGTH_SHORT).show();
				break;
			case FAILED_LOGIN:
				Toast.makeText(getActivity(), "Failed login",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

		}
	}

}
