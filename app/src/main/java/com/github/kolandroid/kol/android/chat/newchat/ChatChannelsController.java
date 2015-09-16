package com.github.kolandroid.kol.android.chat.newchat;

import android.view.View;
import android.widget.ListView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.binders.ChannelBinder;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.adapters.ListAdapter;
import com.github.kolandroid.kol.model.models.chat.ChannelModel;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.util.Callback;

import java.util.ArrayList;

public class ChatChannelsController implements Controller {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -7646035545611799838L;

    private transient ChatConnection connection;
    private transient ListAdapter<ChannelModel> adapter;

    @Override
    public int getView() {
        return R.layout.dialog_chat_channel_list;
    }

    @Override
    public void connect(View view, final Screen host) {
        Callback<ChannelModel> localChannelSelector = new Callback<ChannelModel>() {
            @Override
            public void execute(ChannelModel item) {
                ChatChannelsControllerHost activity = (ChatChannelsControllerHost) host.getActivity();
                activity.switchChannel(item.getName());
                host.close();
            }
        };

        ArrayList<ChannelModel> channels = new ArrayList<ChannelModel>();
        ChannelBinder binder = new ChannelBinder(localChannelSelector);
        adapter = new ListAdapter<ChannelModel>(host.getActivity(), channels, binder);

        ListView list = (ListView) view.findViewById(R.id.dialog_chat_list);
        list.setAdapter(adapter);

        this.connection = new ChatConnection(this.getClass().getSimpleName()) {
            @Override
            public void onConnection(ChatModel model) {
                receivedRefresh(model);
            }

            @Override
            public void receivedRefresh(ChatModel model) {
                if (adapter != null) {
                    adapter.setElements(getAvailableChannels(model));
                }
            }
        };
        connection.connect(host.getActivity());
    }

    private ArrayList<ChannelModel> getAvailableChannels(ChatModel model) {
        ArrayList<ChannelModel> result = new ArrayList<ChannelModel>();
        if (model == null)
            return result;

        for (ChannelModel channel : model.getChannels()) {
            if (channel.isActive() || !channel.getName().contains("@"))
                result.add(channel);
        }
        return result;
    }

    @Override
    public void disconnect(Screen host) {
        if (connection != null)
            connection.close(host.getActivity());
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }

    public interface ChatChannelsControllerHost {
        void switchChannel(String to);
    }
}