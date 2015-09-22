package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.chat.ChannelModel;

public class ChannelCounterController extends LinkedModelController<Void, ChannelModel> {
    private transient TextView badge;

    public ChannelCounterController(ChannelModel channel) {
        super(channel);
    }

    @Override
    public int getView() {
        return R.layout.chat_channel_tab_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        // Do nothing
    }

    @Override
    public void disconnect(Screen host) {
        badge = null;
    }

    @Override
    public void receiveProgress(View view, ChannelModel model, Void message, Screen host) {
        checkNotificationBadge();
    }

    private void checkNotificationBadge() {
        if (badge == null)
            return;

        if (getModel().getUnreadCount() == 0) {
            badge.setVisibility(View.GONE);
        } else {
            badge.setText("" + getModel().getUnreadCount());
            badge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void connect(View view, ChannelModel model, Screen host) {
        TextView nameText = (TextView) view.findViewById(R.id.channelname_text);
        nameText.setText(model.getName());

        badge = (TextView) view.findViewById(R.id.channelname_notification);
        checkNotificationBadge();
    }
}
