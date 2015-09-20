package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.ChatStubModel;
import com.github.kolandroid.kol.util.Logger;

public class ChatCounterController extends ChatStubController {
    private int initialCount = -1;

    public ChatCounterController(Session session) {
        super(new ChatStubModel(session));
    }

    @Override
    public void doConnect(View view, ChatStubModel model, final Screen host) {
        if (model.getMessageCount() > 0) {
            this.initialCount = model.getMessageCount();
        }

        final TextView popup = (TextView) view.findViewById(R.id.gochat_notification);
        if (popup != null) {
            popup.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.log("ChatCounterController", "Opening Chat!");
                ChatController controller = new ChatController(getModel());
                host.getViewContext().getPrimaryRoute().execute(controller);

                initialCount = getModel().getMessageCount();
                popup.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void receiveProgress(View view, ChatStubModel model, Iterable<ChatModelSegment> message, Screen host) {
        int newCount = model.getMessageCount();
        if (initialCount < 0)
            initialCount = newCount; //ignore the first update

        if (newCount > initialCount) {
            TextView popup = (TextView) view.findViewById(R.id.gochat_notification);
            if (popup != null) {
                popup.setVisibility(View.VISIBLE);
                popup.setText("" + (newCount - initialCount));
            }
        }
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
