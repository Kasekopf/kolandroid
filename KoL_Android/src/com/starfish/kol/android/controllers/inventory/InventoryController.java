package com.starfish.kol.android.controllers.inventory;

import android.view.View;

import com.starfish.kol.android.controller.Controller;
import com.starfish.kol.android.controller.GroupController;
import com.starfish.kol.android.controllers.WebController;
import com.starfish.kol.android.screen.DialogScreen;
import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.screen.ScreenSelection;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.inventory.EquipmentPocketModel;
import com.starfish.kol.model.models.inventory.InventoryModel;
import com.starfish.kol.model.models.inventory.InventoryPocketModel;
import com.starfish.kol.model.models.inventory.InventoryVisitor;

public class InventoryController extends GroupController<InventoryPocketModel, InventoryModel> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -275856461187273887L;

	public InventoryController(InventoryModel model) {
		super(model);
	}

	@Override
	public void chooseScreen(ScreenSelection choice) {
		choice.displayPrimary(this);
	}

	private static final InventoryVisitor<Controller> childRoute = new InventoryVisitor<Controller>() {
		@Override
		public Controller display(InventoryPocketModel model) {
			return new InventoryPocketController(model);
		}

		@Override
		public Controller display(EquipmentPocketModel model) {
			return new EquipmentPocketController(model);
		}
	};

	@Override
	public void connect(View view, InventoryModel model, Screen host) {
		super.connect(view, model, host);
		
		WebModel results = model.getResultsPane();
		if(results != null) {
			WebController web = new WebController(results);
			DialogScreen.display(web, host);
		}
	}
	@Override
	public Controller getController(InventoryPocketModel child) {
		return child.execute(childRoute);
	}

}
