package com.github.kolandroid.kol.android.screen;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.MessageController;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.model.models.MessageModel;
import com.github.kolandroid.kol.util.Logger;

public class FragmentScreen extends Fragment implements Screen {
    private Controller controller = null;

    public static Bundle prepare(Controller controller) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("controller", controller);
        return bundle;
    }

    public static FragmentScreen create(Controller controller) {
        FragmentScreen res = new FragmentScreen();
        res.setArguments(FragmentScreen.prepare(controller));
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.controller = (Controller) this.getArguments().getSerializable("controller");

        if (controller == null) {
            Logger.log("FragmentScreen", "Unable to load controller from bundle");
            MessageModel error = new MessageModel("0x35ad1: Unable to display page", MessageModel.ErrorType.ERROR);
            controller = new MessageController(error);
        }

        int layoutId = controller.getView();
        return inflater.inflate(layoutId, container, false);
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.connect(getView(), this);
    }

    @Override
    public void onStop() {
        if (controller != null) {
            controller.disconnect(this);
            this.getArguments().putSerializable("controller", controller); // save the current controller
        }
        super.onStop();
    }

    @Override
    public AndroidViewContext getViewContext() {
        Screen host = (Screen) this.getActivity();
        if (host == null) {
            Logger.log("FragmentScreen", controller + " has an old reference to a closed fragment");
            return null;
        } else {
            return host.getViewContext();
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
