package com.github.kolandroid.kol.android.chat.newchat;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.chat.newchat.ChatActionsController.ChatActionsControllerHost;
import com.github.kolandroid.kol.android.chat.newchat.ChatChannelsController.ChatChannelsControllerHost;
import com.github.kolandroid.kol.android.chat.newchat.ChatSubmissionController.ChatSubmissionControllerHost;
import com.github.kolandroid.kol.android.screen.ActivityScreen;
import com.github.kolandroid.kol.android.screen.DialogScreen;
import com.github.kolandroid.kol.android.screen.FragmentScreen;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.gamehandler.DataContext;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.request.ResponseHandler;

public class ChatActivity extends ActionBarActivity implements ViewContext,
        ChatActionsControllerHost, ChatSubmissionControllerHost, ChatChannelsControllerHost {
    private Screen baseScreen;
    private ViewContext baseContext;

    private ChatController mainChat;
    private ChatSubmissionController chatSubmission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        this.baseContext = new AndroidViewContext(this);
        this.baseScreen = new ActivityScreen(this);

        mainChat = new ChatController();
        FragmentScreen chatScreen = FragmentScreen.create(mainChat);
        getFragmentManager().beginTransaction().replace(R.id.chat_chatscreen, chatScreen).commit();

        chatSubmission = new ChatSubmissionController();
        FragmentScreen subScreen = FragmentScreen.create(chatSubmission);
        getFragmentManager().beginTransaction().replace(R.id.chat_submissionscreen, subScreen).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                ChatChannelsController controller = new ChatChannelsController();
                DialogScreen.display(controller, baseScreen);
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
    public ResponseHandler getPrimaryRoute() {
        return baseContext.getPrimaryRoute();
    }

    @Override
    public LoadingContext createLoadingContext() {
        return baseContext.createLoadingContext();
    }

    @Override
    public DataContext getDataContext() {
        return baseContext.getDataContext();
    }

    @Override
    public void displayMessage(String message) {
        baseContext.displayMessage(message);
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
