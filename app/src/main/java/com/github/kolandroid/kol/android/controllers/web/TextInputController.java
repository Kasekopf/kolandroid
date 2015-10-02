package com.github.kolandroid.kol.android.controllers.web;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.util.Callback;

public class TextInputController implements Controller {
    private final String buttonText;
    private transient final Callback<String> onSubmit;

    public TextInputController(String buttonText, Callback<String> onSubmit) {
        this.onSubmit = onSubmit;
        this.buttonText = buttonText;
    }

    @Override
    public int getView() {
        return R.layout.text_input_view;
    }

    @Override
    public void attach(final View view, final Screen host) {
        Button button = (Button) view.findViewById(R.id.text_input_submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) view
                        .findViewById(R.id.text_input_input);
                String result = input.getText().toString();
                onSubmit.execute(result);
                host.close();
            }
        });
        button.setText(buttonText);
    }

    @Override
    public void connect(View view, Screen host) {
        // Do nothing
    }

    @Override
    public void disconnect(Screen host) {
        // Do nothing
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
