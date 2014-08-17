package com.starfish.kol.android.view;

import android.content.Intent;
import android.os.Bundle;

import com.starfish.kol.model.Model;

public class ModelWrapper {
	private final Model<?> model;
	
	public ModelWrapper(Model<?> model) {
		this.model = model;
	}

	public ModelWrapper(Intent intent) {		
		if (intent.hasExtra("model")) {
			model = (Model<?>) intent.getSerializableExtra("model");
		} else {
			model = null;
		}
	}

	public ModelWrapper(Bundle bundle) {
		if (bundle.containsKey("model")) {
			model = (Model<?>) bundle.getSerializable("model");
		} else {
			model = null;
		}
	}

	public boolean hasModel() {
		return model != null;
	}

	public Model<?> getModel() {
		return model;
	}

	public Class<?> getModelType() {
		if (model == null)
			return null;
		return model.getClass();
	}

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("model", model);
		return bundle;
	}

	public void fillIntent(Intent intent) {
		intent.putExtra("model", model);
	}
	
	public static Bundle bundle(Model<?> model) {
		ModelWrapper wrapper = new ModelWrapper(model);
		return wrapper.toBundle();
	}
}