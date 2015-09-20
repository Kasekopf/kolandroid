package com.github.kolandroid.kol.android.chat;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.chat.ChatActionsController.ChatActionsControllerHost;
import com.github.kolandroid.kol.android.controllers.chat.ChatChannelsController;
import com.github.kolandroid.kol.android.controllers.chat.ChatChannelsController.ChatChannelsControllerHost;
import com.github.kolandroid.kol.android.controllers.chat.ChatController;
import com.github.kolandroid.kol.android.controllers.chat.ChatSubmissionController;
import com.github.kolandroid.kol.android.controllers.chat.ChatSubmissionController.ChatSubmissionControllerHost;
import com.github.kolandroid.kol.android.screen.ActivityScreen;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.FragmentScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.models.chat.ChatStubModel;
import com.github.kolandroid.kol.util.Logger;

public class ChatScreen extends ActivityScreen implements
        ChatActionsControllerHost, ChatSubmissionControllerHost, ChatChannelsControllerHost {
    private ChatController mainChat;
    private ChatSubmissionController chatSubmission;

    @Override
    public void setup(Bundle savedInstanceState) {
        /*
        mainChat = new ChatController();
        FragmentScreen chatScreen = FragmentScreen.create(mainChat);
        getFragmentManager().beginTransaction().replace(R.id.chat_chatscreen, chatScreen).commit();
        */
        /*
        chatSubmission = new ChatSubmissionController();
        FragmentScreen subScreen = FragmentScreen.create(chatSubmission);
        getFragmentManager().beginTransaction().replace(R.id.chat_submissionscreen, subScreen).commit();
        */

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            public void displayPrimary(Controller c, boolean replaceSameType) {
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
                getFragmentManager().beginTransaction().replace(R.id.chat_chatscreen, chatScreen).commit();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_chat_screen;
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public void fillChatText(String text) {
        chatSubmission.fillChatText(text);
    }

    @Override
    public String getCurrentChannel() {
        return mainChat.getCurrentChannel();
    }

    @Override
    public void switchChannel(String to) {
        mainChat.switchChannel(to);
    }
}
