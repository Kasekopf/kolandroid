package com.github.kolandroid.kol.android.controllers.inventory;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.elements.MultiActionElement;

public class MultiActionController extends ModelController<MultiActionElement> {
    private boolean expanded = false;
    private boolean closeOnSubmit;

    public MultiActionController(MultiActionElement model, boolean closeOnSubmit) {
        super(model);

        this.closeOnSubmit = closeOnSubmit;
    }

    @Override
    public void attach(View view, MultiActionElement model, final Screen host) {
        expanded = false;

        final EditText edit = (EditText) view.findViewById(R.id.multiaction_amount);

        final Button one = (Button) view.findViewById(R.id.multiaction_one_submit);
        one.setText(model.getName());
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean submitted;
                if (expanded) {
                    String quantity = edit.getText().toString();
                    submitted = getModel().trigger(quantity);
                } else {
                    submitted = getModel().trigger(1);
                }

                if (submitted && closeOnSubmit) {
                    Activity a = host.getActivity();
                    if (a != null) {
                        InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, 0);
                    }
                    host.close();
                }
            }
        });

        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    one.performClick();
                    return true;
                }
                return false;
            }
        });

        final Button expand = (Button) view.findViewById(R.id.multiaction_more_load);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                one.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                expand.setText("");
                expand.setMinimumWidth(0);
                expand.setWidth(0);
                edit.setVisibility(View.VISIBLE);
                expanded = true;

                if (edit.requestFocus()) {
                    Activity a = host.getActivity();
                    if (a != null) {
                        InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });
        if (model.allowSingleOnly()) {
            expand.setVisibility(View.GONE);
        }
    }

    @Override
    public int getView() {
        return R.layout.multiaction_view;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
