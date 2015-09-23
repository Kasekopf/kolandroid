package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.adapters.ListControllerAdapter;
import com.github.kolandroid.kol.model.models.chat.ChannelModel;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatStubModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatChannelsController extends ChatStubController<ChatStubModel> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -7646035545611799838L;

    private final Set<String> currentChannels;
    private final ArrayList<ChannelListItemController> controllers;

    private transient ListControllerAdapter<ChannelListItemController> adapter;

    public ChatChannelsController(ChatModel model) {
        super(new ChatStubModel(model));

        controllers = new ArrayList<>();
        currentChannels = new HashSet<>();
    }

    @Override
    public int getView() {
        return R.layout.chat_channel_list_view;
    }

    @Override
    public void doConnect(View view, ChatStubModel model, final Screen host) {
        for (ChannelModel channel : model.getChannels()) {
            addChannel(channel, host);
        }

        adapter = new ListControllerAdapter<>(host, controllers);
        ListView list = (ListView) view.findViewById(R.id.chat_channel_list_list);
        list.setAdapter(adapter);
    }

    public void addChannel(ChannelModel channel, Screen host) {
        if (currentChannels.contains(channel.getName()))
            return;
        if (channel.getName().contains("@") && !channel.isActive())
            return;
        currentChannels.add(channel.getName());

        ChannelListItemController controller = new ChannelListItemController(channel);
        controllers.add(controller);
    }

    @Override
    public void disconnect(Screen host) {
        this.adapter = null;
        super.disconnect(host);
    }

    @Override
    public void receiveProgress(View view, ChatStubModel model, Iterable<ChatModelSegment> message, Screen host) {
        for (ChannelModel c : model.getChannels()) {
            addChannel(c, host);
        }

        if (adapter != null) {
            //Reset this every time; the list adapter does strange things with connect/disconnect
            // so we cannot rely on each ChannelController updating properly
            adapter.setElements(controllers);
        }
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }

    private class ChannelListItemController extends ChannelCounterController {
        private boolean active;

        public ChannelListItemController(ChannelModel channel) {
            super(channel);
        }

        @Override
        public int getView() {
            return R.layout.chat_channel_summary_view;
        }

        @Override
        public void receiveProgress(View view, ChannelModel model, Void message, Screen host) {
            if (active != model.isActive()) {
                setButtonState(view, model, host);
            }
            super.receiveProgress(view, model, message, host);
        }

        private void setButtonState(View view, ChannelModel model, final Screen host) {
            Button enter = (Button) view.findViewById(R.id.chat_channel_summary_enter);
            Button leave = (Button) view.findViewById(R.id.chat_channel_summary_leave);

            active = model.isActive();
            if (model.isActive()) {
                enter.setEnabled(false);
                leave.setEnabled(true);
                leave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        getModel().leave();
                    }
                });
            } else {
                enter.setEnabled(true);
                leave.setEnabled(false);
                enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        getModel().enter();
                    }
                });
            }
        }

        @Override
        public void connect(View view, ChannelModel model, final Screen host) {
            super.connect(view, model, host);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getModel().isActive()) {
                        getModel().makePrimaryChannel();
                    }
                }
            });

            this.setButtonState(view, model, host);
        }

        @Override
        public String toString() {
            return getModel().getName();
        }
    }
}
