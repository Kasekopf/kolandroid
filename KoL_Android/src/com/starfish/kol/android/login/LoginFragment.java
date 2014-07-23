package com.starfish.kol.android.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.starfish.kol.android.R;
import com.starfish.kol.android.chat.ChatService;
import com.starfish.kol.android.game.BaseGameFragment;
import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.model.models.login.LoginModel;
import com.starfish.kol.model.models.login.LoginStatus;
import com.starfish.kol.model.models.login.PasswordHash;

public class LoginFragment extends BaseGameFragment<LoginStatus, LoginModel> {
	protected static final String LOGIN_STORAGE = "KoLLogin";
	
	private String savedUser = null;
	private PasswordHash savedPass = null;
	
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
		final CheckBox check = (CheckBox) view.findViewById(R.id.login_savepassword);			
		
        SharedPreferences settings = getActivity().getSharedPreferences(LOGIN_STORAGE, 0);
        user.setText(settings.getString("username", ""));
        check.setChecked(settings.getBoolean("savepass", true));
        
        if(settings.contains("password")) {
        	pass.setHint("\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022"); //unicode dot x10
        	
        	savedUser = settings.getString("username", "");
        	savedPass = new PasswordHash(settings.getString("password", ""), true);
        }
		
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
				
				PasswordHash pass;
				
				if(username == null || username.length() == 0)
					return;
				
				if(savedUser != null && username.equalsIgnoreCase(savedUser) && (password == null || password.length() == 0)) {
					pass = savedPass;
				} else {
					if (password == null || password.length() == 0)
						return;
					pass = new PasswordHash(password, false);
				}

				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);

				getModel().login(new AndroidViewContext(LoginFragment.this.getActivity()), username, pass);
				

			    SharedPreferences settings = getActivity().getSharedPreferences(LOGIN_STORAGE, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putBoolean("savepass", check.isChecked());
			    if(check.isChecked()) {
					Log.i("Credentials", "Saving username: " + username);
					Log.i("Credentials", "Saving password hash: " + pass.getBaseHash());
			    	editor.putString("username", username);
			    	editor.putString("password", pass.getBaseHash());
			    } else {
			    	editor.remove("username");
			    	editor.remove("password");
			    }
			    editor.commit();
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
			i.putExtra("session", getModel().getSession());
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
