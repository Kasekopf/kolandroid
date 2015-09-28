package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.chat.ChannelModel;

public class ChannelCounterController extends LinkedModelController<Void, ChannelModel> {
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
    public void receiveProgress(View view, ChannelModel model, Void message, Screen host) {
        checkSelected(view, model);
        checkNotificationBadge((TextView) view.findViewById(R.id.chat_channel_notification));
    }

    private void checkNotificationBadge(TextView badge) {
        if (badge == null)
            return;

        if (getModel().getUnreadCount() == 0 || getModel().isPrimaryChannel()) {
            badge.setVisibility(View.GONE);
        } else {
            badge.setText("" + getModel().getUnreadCount());
            badge.setVisibility(View.VISIBLE);
        }
    }

    private void checkSelected(View view, ChannelModel model) {
        if (model.isPrimaryChannel()) {
            view.setBackgroundColor(0xFFAAAAAA);
        } else {
            view.setBackgroundColor(0xFFFFFFFF);
        }
    }

    @Override
    public void attach(View view, ChannelModel model, Screen host) {
        TextView nameText = (TextView) view.findViewById(R.id.chat_channel_name);
        nameText.setText(model.getName());

        checkSelected(view, model);
        checkNotificationBadge((TextView) view.findViewById(R.id.chat_channel_notification));
    }
}
