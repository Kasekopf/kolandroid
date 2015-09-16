package com.github.kolandroid.kol.android.screen;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.ErrorController;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.util.Logger;

public class DialogScreen extends DialogFragment implements Screen {
    private Controller controller = null;

    public static Bundle prepare(Controller controller) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("controller", controller);
        return bundle;
    }

    public static Bundle prepare(Controller controller, String title) {
        Bundle bundle = DialogScreen.prepare(controller);
        bundle.putString("title", title);
        return bundle;
    }

    public static DialogScreen display(Controller controller, Screen base) {
        DialogScreen res = new DialogScreen();
        res.setArguments(DialogScreen.prepare(controller));
        res.show(base.getFragmentManager(), "dialog");
        return res;
    }

    public static DialogScreen display(Controller controller, Screen base,
                                       String title) {
        DialogScreen res = new DialogScreen();
        res.setArguments(DialogScreen.prepare(controller, title));
        res.show(base.getFragmentManager(), "dialog");
        return res;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        if (getArguments().containsKey("title")) {
            d.setTitle(getArguments().getString("title"));
        } else {
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.controller = (Controller) this.getArguments().getSerializable("controller");

        if (controller == null) {
            Logger.log("DialogScreen", "Unable to load controller from bundle");
            controller = new ErrorController("[DialogScreen] Unable to load controller from bundle");
        }

        int layoutid = controller.getView();
        View view = inflater.inflate(layoutid, container, false);
        controller.connect(view, this);
        return view;
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public void onDestroyView() {
        if (controller != null)
            controller.disconnect(this);
        super.onDestroyView();
    }

    @Override
    public ViewContext getViewContext() {
        return (ViewContext) this.getActivity();
    }

    @Override
    public void close() {
        this.dismiss();
    }
}