package com.github.kolandroid.kol.android.controllers;

import android.view.View;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.models.ErrorModel;

public class ErrorController extends ModelController<ErrorModel> {
    public ErrorController(String message, ErrorModel.ErrorType type) {
        this(new ErrorModel(message, type));
    }

    public ErrorController(ErrorModel model) {
        super(model);
    }

    @Override
    public int getView() {
        return R.layout.dialog_error_screen;
    }

    @Override
    public void connect(View view, ErrorModel model, Screen host) {
        view.setBackgroundColor(model.visitType(new ErrorModel.ErrorTypeVisitor<Integer>() {
            @Override
            public Integer forMessage() {
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

        TextView title = (TextView) view.findViewById(R.id.error_type);
        title.setText(model.visitType(new ErrorModel.ErrorTypeVisitor<CharSequence>() {
            @Override
            public CharSequence forMessage() {
                return "Message:";
            }

            @Override
            public CharSequence forError() {
                return "Error:";
            }

            @Override
            public CharSequence forSevere() {
                return "Error:";
            }
        }));
        TextView txtMessage = (TextView) view.findViewById(R.id.error_info);
        txtMessage.setText(model.getMessage());
    }

    @Override
    public void disconnect(Screen host) {
        //do nothing
    }

    @Override
    public void chooseScreen(final ScreenSelection choice) {
        getModel().visitType(new ErrorModel.ErrorTypeVisitor<Void>() {
            @Override
            public Void forMessage() {
                choice.displayDialog(ErrorController.this);
                return null;
            }

            @Override
            public Void forError() {
                choice.displayDialog(ErrorController.this);
                return null;
            }

            @Override
            public Void forSevere() {
                choice.displayExternalDialog(ErrorController.this);
                return null;
            }
        });
    }

}
