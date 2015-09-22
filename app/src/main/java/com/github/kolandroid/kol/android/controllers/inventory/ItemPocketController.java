package com.github.kolandroid.kol.android.controllers.inventory;

import android.view.View;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.binders.ColoredGroupBinder;
import com.github.kolandroid.kol.android.binders.SubtextBinder;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.screen.ViewScreen;
import com.github.kolandroid.kol.android.util.HandlerCallback;
import com.github.kolandroid.kol.android.util.searchlist.GroupSearchListController;
import com.github.kolandroid.kol.android.util.searchlist.ListSelector;
import com.github.kolandroid.kol.model.LiveModel.LiveMessage;
import com.github.kolandroid.kol.model.models.inventory.ItemModel;
import com.github.kolandroid.kol.model.models.inventory.ItemPocketModel;

public class ItemPocketController extends
        LinkedModelController<LiveMessage, ItemPocketModel> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 6843753318299187981L;
    private final int groupColor;
    private transient GroupSearchListController<ItemModel> list;
    private transient HandlerCallback<ItemModel> displayModel;
    public ItemPocketController(ItemPocketModel model, int groupColor) {
        super(model);
        this.groupColor = groupColor;
    }

    @Override
    public int getView() {
        return R.layout.fragment_inventory_pane;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayPrimary(this, true);
    }

    @Override
    public void receiveProgress(View view, ItemPocketModel model, LiveMessage message, Screen host) {
        if (list != null)
            list.setItems(model.getItems());
    }

    @Override
    public void disconnect(Screen host) {
        super.disconnect(host);
        displayModel.close();
        list = null;
    }

    @Override
    public void connect(View view, ItemPocketModel model, final Screen host) {
        ViewScreen screen = (ViewScreen) view.findViewById(R.id.inventory_list);
        list = new GroupSearchListController<>(model.getItems(), new ColoredGroupBinder(groupColor), SubtextBinder.ONLY, new ListSelector<ItemModel>() {
            @Override
            public boolean selectItem(Screen host, ItemModel item) {
                item.attachView(host.getViewContext());
                item.loadDescription(displayModel.weak());
                return true;
            }
        });
        screen.display(list, host);

        displayModel = new HandlerCallback<ItemModel>() {
            @Override
            protected void receiveProgress(ItemModel message) {
                ItemController controller = new ItemController(message);
                host.getViewContext().getPrimaryRoute().execute(controller);
            }
        };
    }
}
