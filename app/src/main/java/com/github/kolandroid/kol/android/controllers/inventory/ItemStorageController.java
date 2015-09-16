package com.github.kolandroid.kol.android.controllers.inventory;

import android.view.View;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controller.GroupController;
import com.github.kolandroid.kol.android.controllers.web.WebController;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.model.models.inventory.EquipmentPocketModel;
import com.github.kolandroid.kol.model.models.inventory.ItemPocketModel;
import com.github.kolandroid.kol.model.models.inventory.ItemStorageModel;
import com.github.kolandroid.kol.model.models.inventory.PocketVisitor;

public class ItemStorageController<E extends ItemStorageModel> extends GroupController<ItemPocketModel, E> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -275856461187273887L;

    private final int groupColor;

    public ItemStorageController(E model) {
        this(model, R.color.inventory_header);
    }

    public ItemStorageController(E model, int groupColor) {
        super(model);
        this.groupColor = groupColor;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayPrimary(this);
    }

    @Override
    public void connect(View view, E model, Screen host) {
        super.connect(view, model, host);

        WebModel results = model.getResultsPane();
        if (results != null) {
            WebController web = new WebController(results);
            DialogScreen.display(web, host);
        }
    }

    @Override
    public Controller getController(ItemPocketModel child) {
        return child.execute(new PocketVisitor<Controller>() {
            @Override
            public Controller display(ItemPocketModel model) {
                return new ItemPocketController(model, groupColor);
            }

            @Override
            public Controller display(EquipmentPocketModel model) {
                return new EquipmentPocketController(model, groupColor);
            }
        });
    }

}