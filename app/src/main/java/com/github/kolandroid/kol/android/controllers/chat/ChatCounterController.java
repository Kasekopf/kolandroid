package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.models.chat.ChannelModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatStubModel;
import com.github.kolandroid.kol.util.Logger;

public class ChatCounterController extends ChatStubController<ChatStubModel> {
    private transient TextView popup;

    public ChatCounterController(Session session) {
        super(new ChatStubModel(session));
    }

    @Override
    public void doConnect(View view, ChatStubModel model, final Screen host) {
        popup = (TextView) view.findViewById(R.id.gochat_notification);
        if (popup != null) {
            popup.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.log("ChatCounterController", "Opening Chat!");
                ChatController controller = new ChatController(getModel());
                host.getViewContext().getPrimaryRoute().execute(controller);

                popup.setVisibility(View.GONE);
            }
        });
    }

    private void checkUnread() {
        int totalUnread = 0;
        for (ChannelModel channel : getModel().getChannels()) {
            totalUnread += channel.getUnreadCount();
        }

        if (popup != null) {
            if (totalUnread == 0) {
                popup.setVisibility(View.GONE);
            } else {
                popup.setVisibility(View.VISIBLE);
                popup.setText("" + totalUnread);
            }
        }
    }

    @Override
    public void receiveProgress(View view, ChatStubModel model, Iterable<ChatModelSegment> message, Screen host) {
        checkUnread();
    }

    @Override
    public int getView() {
        return R.layout.view_game_gochat;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
