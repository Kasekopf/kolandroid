package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.LinkedModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.chat.ChannelModel;

public class ChannelCounterController extends LinkedModelController<Void, ChannelModel> {
    private int count = 0;

    public ChannelCounterController(ChannelModel channel) {
        super(channel);
    }

    @Override
    public int getView() {
        return R.layout.view_chat_channelname;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        // Do nothing
    }

    @Override
    public void receiveProgress(View view, ChannelModel model, Void message, Screen host) {
        count += 1;

        TextView notification = (TextView) view.findViewById(R.id.channelname_notification);
        notification.setText("" + count);
        notification.setVisibility(View.VISIBLE);
    }

    @Override
    public void connect(View view, ChannelModel model, Screen host) {
        TextView nameText = (TextView) view.findViewById(R.id.channelname_text);
        nameText.setText(model.getName());

        TextView notification = (TextView) view.findViewById(R.id.channelname_notification);
        notification.setText("");
        notification.setVisibility(View.GONE);
    }
}
