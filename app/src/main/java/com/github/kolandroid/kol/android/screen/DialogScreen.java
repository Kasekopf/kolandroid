package com.github.kolandroid.kol.android.screen;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.MessageController;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.model.models.MessageModel;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("title")) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
        }
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

            MessageModel error = new MessageModel("0x35ad2: Unable to display page", MessageModel.ErrorType.ERROR);
            controller = new MessageController(error);
            Logger.log("DialogScreen", "Unable to load controller from bundle");
        }

        int layoutId = controller.getView();
        View view = inflater.inflate(layoutId, container, false);
        controller.attach(view, this);
        return view;
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (controller != null) {
            controller.connect(getView(), this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (controller != null) {
            controller.disconnect(this);
        }
    }

    @Override
    public AndroidViewContext getViewContext() {
        Screen host = (Screen) this.getActivity();
        return host.getViewContext();
    }

    @Override
    public void close() {
        if (controller != null)
            controller.disconnect(this);
        this.dismiss();
    }
}
