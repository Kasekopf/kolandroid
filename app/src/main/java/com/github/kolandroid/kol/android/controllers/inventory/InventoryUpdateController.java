package com.github.kolandroid.kol.android.controllers.inventory;

import android.view.View;

import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.controller.UpdateController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.inventory.InventoryUpdateModel;

public class InventoryUpdateController extends ModelController<InventoryUpdateModel> implements UpdateController<InventoryUpdateModel> {
    public InventoryUpdateController(InventoryUpdateModel model) {
        super(model);
    }

    @Override
    public void attach(View view, InventoryUpdateModel model, Screen host) {

    }

    @Override
    public Class<InventoryUpdateModel> getUpdateType() {
        return InventoryUpdateModel.class;
    }

    @Override
    public int getView() {
        return 0;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayPrimaryUpdate(this, false);
    }
}
