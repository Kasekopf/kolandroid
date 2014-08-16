package com.starfish.kol.android.controllers;

import android.view.View;

import com.starfish.kol.android.R;
import com.starfish.kol.android.controller.Controller;
import com.starfish.kol.android.controller.GroupController;
import com.starfish.kol.android.controller.ModelController;
import com.starfish.kol.android.screen.DialogScreen;
import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.screen.ScreenSelection;
import com.starfish.kol.android.screen.ViewScreen;
import com.starfish.kol.model.LiveMessage;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.CraftingModel.CraftingSubModel;
import com.starfish.kol.model.models.WebModel;

public class CraftingController extends
		GroupController<CraftingSubModel, CraftingModel> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -2070983265448333129L;

	public CraftingController(CraftingModel model) {
		super(model);
	}

	@Override
	public void chooseScreen(ScreenSelection choice) {
		choice.displayPrimary(this);
	}

	@Override
	public void connect(View view, CraftingModel model, Screen host) {
		super.connect(view, model, host);
		
		WebModel results = model.getResultsPane();
		if(results != null) {
			WebController web = new WebController(results);
			DialogScreen.display(web, host);
		}
	}
	
	@Override
	protected Controller getController(CraftingSubModel child) {
		return new CraftingSubController(child);
	}

	private static class CraftingSubController extends
			ModelController<LiveMessage, CraftingSubModel> {
		/**
		 * Autogenerated by eclipse.
		 */
		private static final long serialVersionUID = -7994839681352351331L;

		private WebController web;
		
		public CraftingSubController(CraftingSubModel model) {
			super(model);
		}

		@Override
		public int getView() {
			return R.layout.fragment_crafting_subscreen;
		}

		@Override
		public void chooseScreen(ScreenSelection choice) {
			choice.displayPrimary(this);
		}

		@Override
		public void connect(View view, CraftingSubModel model, Screen host) {
			web = new WebController(model.getBaseModel());
			ViewScreen screen = (ViewScreen)view;
			screen.display(web, host);
		}
		
		@Override
		public void recieveProgress(View view, CraftingSubModel model, LiveMessage message, Screen host) {
			if(web != null)
				web.updateModel(model.getBaseModel());
		}

	}
}
