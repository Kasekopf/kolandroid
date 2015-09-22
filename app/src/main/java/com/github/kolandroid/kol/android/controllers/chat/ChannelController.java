package com.github.kolandroid.kol.android.controllers.chat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.binders.ChatBinder;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.util.adapters.ListAdapter;
import com.github.kolandroid.kol.model.models.chat.ChannelModel;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.ChatText;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatStubModel;

import java.util.ArrayList;
import java.util.List;

public class ChannelController extends ChatStubController<ChatStubModel> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 2772987489714420525L;

    private final String name;

    private transient ListAdapter<ChatText> adapter = null;

    public ChannelController(ChatModel model, String tag) {
        super(new ChatStubModel(model));
        this.name = tag;
    }

    @Override
    public void doConnect(View view, ChatStubModel model, final Screen host) {
        List<ChatText> messages = new ArrayList<>();
        adapter = new ListAdapter<>(view.getContext(), messages,
                ChatBinder.ONLY);

        ListView list = (ListView) view
                .findViewById(R.id.chat_channel_message_list);
        list.setAdapter(adapter);
        list.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> myAdapter, View myView,
                                    int myItemInt, long myLong) {
                final ChatText choice = (ChatText) myAdapter
                        .getItemAtPosition(myItemInt);

                if (choice.getActions().size() == 0)
                    return;

                ChatActionsController controller = new ChatActionsController(getModel(), choice);
                host.getViewContext().getPrimaryRoute().execute(controller);
            }
        });
    }

    @Override
    public void receiveProgress(View view, ChatStubModel model, Iterable<ChatModelSegment> message, Screen host) {
        if (adapter == null)
            return;

        ChannelModel channel = model.getChannel(name);
        if (channel != null) {
            channel.readAllMessages();
            adapter.setElements(channel.getMessages());
        }
    }

    @Override
    public int getView() {
        return R.layout.chat_channel_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayPrimary(this, true);
    }
}
