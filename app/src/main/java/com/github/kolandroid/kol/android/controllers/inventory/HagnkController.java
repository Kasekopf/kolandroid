package com.github.kolandroid.kol.android.controllers.inventory;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.model.models.inventory.HagnkModel;

public class HagnkController extends ItemStorageController<HagnkModel> {
    public HagnkController(HagnkModel model) {
        super(model, R.color.hagnk_header);
    }

    @Override
    public Class<HagnkModel> getUpdateType() {
        return HagnkModel.class;
    }
}
