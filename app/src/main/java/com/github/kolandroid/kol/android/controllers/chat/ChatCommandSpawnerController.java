package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;

import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatCommandSpawnerStubModel;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatStubModel;

public class ChatCommandSpawnerController extends ChatStubController<ChatCommandSpawnerStubModel> {
    public ChatCommandSpawnerController(ChatStubModel model) {
        super(new ChatCommandSpawnerStubModel(model));
    }

    @Override
    public void receiveProgress(View view, ChatCommandSpawnerStubModel model, Iterable<ChatModelSegment> message, Screen host) {
        // do nothing
    }

    @Override
    public void attach(View view, ChatCommandSpawnerStubModel model, Screen host) {
        // do nothing
    }

    @Override
    public int getView() {
        return 0;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        // do nothing
    }
}
