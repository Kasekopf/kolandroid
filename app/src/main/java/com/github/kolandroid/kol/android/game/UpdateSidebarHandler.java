package com.github.kolandroid.kol.android.game;

import com.github.kolandroid.kol.android.util.HandlerCallback;

public class UpdateSidebarHandler extends HandlerCallback<Void> {
    private final SidebarController controller;

    public UpdateSidebarHandler(SidebarController controller) {
        this.controller = controller;
    }

    @Override
    protected void receiveProgress(Void message) {
        controller.refresh();
    }
}
