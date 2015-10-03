package com.github.kolandroid.kol.android.chat;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controller.UpdateController;
import com.github.kolandroid.kol.android.controllers.chat.ChatChannelsController;
import com.github.kolandroid.kol.android.controllers.chat.ChatCommandSpawnerController;
import com.github.kolandroid.kol.android.screen.ActivityScreen;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.FragmentScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatStubModel;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Logger;

public class ChatScreen extends ActivityScreen {
    private Controller chatCommandSpawner;

    @Override
    public Controller setup(Bundle savedInstanceState, Controller initial) {
        overridePendingTransition(R.anim.inleftanim, R.anim.outleftanim);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        if (chatCommandSpawner == null) {
            chatCommandSpawner = new ChatCommandSpawnerController(new ChatStubModel(new Session()));
        }
        chatCommandSpawner.attach(null, this);

        return initial;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (chatCommandSpawner != null) {
            chatCommandSpawner.connect(null, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (chatCommandSpawner != null) {
            chatCommandSpawner.disconnect(this);
        }
    }

    @Override
    protected void displayController(Controller controller, boolean addToBackStack) {
        controller.chooseScreen(new ScreenSelection() {
            @Override
            public void displayExternal(Controller c) {
                Logger.log("ChatScreen", "ERROR: Controller " + c + " has chosen to appear on an external screen. Ignoring.");
            }

            @Override
            public void displayExternalDialog(Controller c, boolean cancellable) {
                Logger.log("ChatScreen", "ERROR: Controller " + c + " has chosen to appear on an external dialog. Ignoring.");
            }

            @Override
            public void displayPrimaryUpdate(UpdateController c, boolean displayIfUnable) {
                Logger.log("ChatScreen", "ERROR: Controller " + c + " has chosen to update a primary screen. Ignoring.");
            }

            @Override
            public void displayPrimary(Controller c) {
                Logger.log("ChatScreen", "ERROR: Controller " + c + " has chosen to appear on a primary screen. Ignoring.");
            }

            @Override
            public void displayDialog(Controller c) {
                Logger.log("ChatScreen", "Displaying " + c + " on new dialog box");
                DialogScreen.display(c, ChatScreen.this);
            }

            @Override
            public void displayChat(Controller c) {
                Logger.log("ChatScreen", "Displaying " + c + " on chat pane");

                FragmentScreen chatScreen = FragmentScreen.create(c);
                getFragmentManager().beginTransaction().replace(R.id.chat_main_screen, chatScreen).commit();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_chat_view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.inrightanim, R.anim.outrightanim);
                return true;
            case R.id.action_channels:
                ChatChannelsController controller = new ChatChannelsController(new ChatStubModel(new Session())); //TODO
                this.getViewContext().getPrimaryRoute().execute(controller);
                return true;
            case R.id.action_disconnect:
                ChatBroadcaster.sendCommand(this, ChatModel.ChatModelCommand.StopChat.ONLY);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_screen, menu);
        return true;
    }
}
