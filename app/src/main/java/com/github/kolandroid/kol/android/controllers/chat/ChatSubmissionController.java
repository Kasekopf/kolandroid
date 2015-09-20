package com.github.kolandroid.kol.android.controllers.chat;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.chat.ChatModel;
import com.github.kolandroid.kol.model.models.chat.ChatModelSegment;
import com.github.kolandroid.kol.model.models.chat.ChatStubModel;

public class ChatSubmissionController extends ChatStubController {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -1682070095158477350L;

    private transient EditText text;

    public ChatSubmissionController(ChatModel model) {
        super(new ChatStubModel(model));
    }

    @Override
    public int getView() {
        return R.layout.fragment_chat_submission;
    }

    @Override
    public void doConnect(View view, ChatStubModel model, final Screen host) {
        text = (EditText) view.findViewById(R.id.chatroom_text_input);

        Button submit = (Button) view.findViewById(R.id.chatroom_submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ChatSubmissionControllerHost activity = (ChatSubmissionControllerHost) host
                        .getActivity();
                String msg = text.getText().toString();
                String channel = activity.getCurrentChannel();

                ChatModel chat = getModel();
                if (chat != null) {
                    chat.submitChat(channel, msg);
                    text.setText("");
                }

                InputMethodManager inputManager = (InputMethodManager) host
                        .getActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(host.getActivity()
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    @Override
    public void receiveProgress(View view, ChatStubModel model, Iterable<ChatModelSegment> message, Screen host) {
        // do nothing
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }

    public void fillChatText(String with) {
        if (text != null) {
            text.setText(with);
        }
    }

    public interface ChatSubmissionControllerHost {
        String getCurrentChannel();
    }
}
