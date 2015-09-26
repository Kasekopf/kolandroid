package com.github.kolandroid.kol.android.controllers.inventory;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.model.models.inventory.InventoryModel;

public class InventoryController extends ItemStorageController<InventoryModel> {
    public InventoryController(InventoryModel model) {
        super(model, R.color.inventory_header);
    }

    @Override
    public Class<InventoryModel> getUpdateType() {
        return InventoryModel.class;
    }
}
