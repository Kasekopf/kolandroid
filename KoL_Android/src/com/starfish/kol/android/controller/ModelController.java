package com.starfish.kol.android.controller;

import android.view.View;

import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.util.AndroidProgressHandler;
import com.starfish.kol.model.Model;

public abstract class ModelController<C, M extends Model<C>> implements Controller {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 5761904333216584549L;
	
	private M model;
	private transient AndroidProgressHandler<C> callback;
	
	public ModelController(M model) {
		this.model = model;
	}
	
	@Override
	public void connect(final View view, final Screen host) {
		this.callback = new AndroidProgressHandler<C>() {
			@Override
			public void recieveProgress(C message) {
				ModelController.this.recieveProgress(view, model, message, host);
			}
		};
		
		this.model.connectView(callback, host.getViewContext());
		
		this.connect(view, model, host);
	}
	
	public abstract void connect(View view, M model, Screen host);
	public void recieveProgress(View view, M model, C message, Screen host) {
		// do nothing by default.
	}
	
	protected void changeModel(M model) {
		this.model = model;
	}
	
	@Override
	public void disconnect() {
		callback.close();
	}

	public M getModel() {
		return model;
	}
}
