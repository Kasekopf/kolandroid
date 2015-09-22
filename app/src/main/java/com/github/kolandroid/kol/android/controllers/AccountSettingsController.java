package com.github.kolandroid.kol.android.controllers;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controller.GroupController;
import com.github.kolandroid.kol.android.controllers.web.LiveWebController;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.AccountSettingsModel;
import com.github.kolandroid.kol.model.models.LiveWebModel;

public class AccountSettingsController extends GroupController<LiveWebModel, AccountSettingsModel> {
    public AccountSettingsController(AccountSettingsModel model) {
        super(model);
    }

    @Override
    protected Controller getController(LiveWebModel child) {
        return new LiveWebController(child);
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayPrimary(this, true);
    }
}
