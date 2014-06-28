package com.starfish.kol.android.game;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.starfish.kol.android.view.ApplicationView;
import com.starfish.kol.model.Model;

public abstract class GameFragment extends DialogFragment {
	private int layoutid;	
	
	public GameFragment(int layoutid) {
		this.layoutid = layoutid;
	}
	
	public static Bundle getModelBundle(Model<?> m) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("modeltype", m.getClass());
		bundle.putSerializable("model", m);
		return bundle;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  Dialog dialog = super.onCreateDialog(savedInstanceState);

	  // request a window without the title
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(layoutid, container, false);

		Log.i("GameFragment", "Loaded new view " + this.getClass().toString());
		Model<?> model = (Model<?>) this.getArguments().getSerializable("model");
		if(model == null) {
			Log.i("BaseGameFragment", "Model was null in " + this.getClass());
			return rootView;
		}
		
		ApplicationView app = (ApplicationView) getActivity().getApplication();
		app.connectModel(model);

		this.doCreateSetup(rootView, model, savedInstanceState);
		return rootView;
	}
	
	public abstract void doCreateSetup(View view, Model<?> model, Bundle savedInstanceState);
	public abstract Model<?> getModel();
	public Class<?> getModelType() {
		return (Class<?>)this.getArguments().getSerializable("modeltype");
	}
	
	public void invalidateStats() {
		GameCallbacks toActivity = (GameCallbacks)this.getActivity();
		toActivity.refreshStats();
	}
	
	public static interface GameCallbacks
	{
		public void refreshStats();
	}
}
