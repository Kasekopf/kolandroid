package com.starfish.kol.android.game;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.starfish.kol.android.util.AndroidProgressHandler;
import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.android.view.ModelWrapper;
import com.starfish.kol.model.Model;

public abstract class GameFragment<C, M extends Model<C>> extends DialogFragment {
	private int layoutid;	
	
	private M base;
	private AndroidProgressHandler<C> callback;
	
	public GameFragment(int layoutid) {
		this.layoutid = layoutid;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  Dialog dialog = super.onCreateDialog(savedInstanceState);

	  // request a window without the title
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  return dialog;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(layoutid, container, false);

		Log.i("GameFragment", "Loaded new view " + this.getClass().toString());
		
		if(this.base == null) {
			//Fragment created from scratch 
			ModelWrapper wrapper = new ModelWrapper(this.getArguments());
			this.base = (M)wrapper.extractModel();
			if(this.base == null) {
				Log.i("BaseGameFragment", "Model was null in " + this.getClass());
				return rootView;
			}
		}		
		
		this.callback = new AndroidProgressHandler<C>() {
			@Override
			public void recieveProgress(C message) {
				GameFragment.this.recieveProgress(message);
			}
		};
		
		base.connectView(callback, new AndroidViewContext(this.getActivity()));
		this.onCreateSetup(rootView, base, savedInstanceState);
		
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		if(callback != null)
			callback.close();
		Log.i("BaseGameFragment", "View destroyed for " + this.getClass());
		super.onDestroyView();
	}

	public final M getModel() {
		return base;
	}
	
	public abstract void onCreateSetup(View view, M base, Bundle savedInstanceState);
	protected abstract void recieveProgress(C message);
}
