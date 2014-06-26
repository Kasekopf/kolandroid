package com.starfish.kol.android.game;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.starfish.kol.android.util.AndroidProgressHandler;
import com.starfish.kol.model.Model;

public abstract class BaseGameFragment<C, M extends Model<C>> extends GameFragment {
	private M base;
	private AndroidProgressHandler<C> callback;
	
	public BaseGameFragment(int layoutid) {
		super(layoutid);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void doCreateSetup(View view, Model<?> model, Bundle savedInstanceState) {
		this.base = (M)model;

		Log.i("BaseGameFragment", "View created for " + this.getClass());
		
		this.callback = new AndroidProgressHandler<C>() {
			@Override
			public void recieveProgress(C message) {
				BaseGameFragment.this.recieveProgress(message);
			}
		};
		
		base.connectView(callback);
		
		this.onCreateSetup(view, base, savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		callback.close();
		Log.i("BaseGameFragment", "View destroyed for " + this.getClass());
		super.onDestroyView();
	}
	
	public abstract void onCreateSetup(View view, M base, Bundle savedInstanceState);

	protected abstract void recieveProgress(C message);
	
	@Override
	public final M getModel() {
		return base;
	}
}
