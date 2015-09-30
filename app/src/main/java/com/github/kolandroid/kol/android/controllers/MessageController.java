package com.github.kolandroid.kol.android.controllers;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.MessageModel;

public class MessageController<E extends MessageModel> extends ModelController<E> {
    public MessageController(E model) {
        super(model);
    }

    @Override
    public void attach(View view, E model, Screen host) {
        view.setBackgroundColor(model.visitErrorType(new MessageModel.ErrorTypeVisitor<Integer>() {
            @Override
            public Integer forNone() {
                return 0xFF0000FF;
            }

            @Override
            public Integer forError() {
                return 0xFFBB0000;
            }

            @Override
            public Integer forSevere() {
                return 0xFFBB0000;
            }
        }));

        TextView title = (TextView) view.findViewById(R.id.message_title);
        title.setText(model.getTitle());

        TextView txtMessage = (TextView) view.findViewById(R.id.message_text);
        txtMessage.setText(model.getMessage());

        Button actionButton = (Button) view.findViewById(R.id.message_action);
        if (model.hasAction()) {
            actionButton.setText(model.getActionText());
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getModel().triggerAction();
                }
            });
        } else {
            actionButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getView() {
        return R.layout.message_view;
    }

    @Override
    public void chooseScreen(final ScreenSelection choice) {
        getModel().visitErrorType(new MessageModel.ErrorTypeVisitor<Void>() {
            @Override
            public Void forNone() {
                choice.displayDialog(MessageController.this);
                return null;
            }

            @Override
            public Void forError() {
                choice.displayDialog(MessageController.this);
                return null;
            }

            @Override
            public Void forSevere() {
                choice.displayExternalDialog(MessageController.this, true);
                return null;
            }
        });
    }
}
