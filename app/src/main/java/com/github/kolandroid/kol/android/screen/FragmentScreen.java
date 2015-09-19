package com.github.kolandroid.kol.android.screen;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.ErrorController;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.models.ErrorModel;
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
            controller = new ErrorController("0x35ad1: Unable to display page", ErrorModel.ErrorType.ERROR);
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
        if (controller != null) {
            this.getArguments().putSerializable("controller", controller); // save the current controller
        }
        super.onDestroyView();
    }

    @Override
    public ViewContext getViewContext() {
        if (this.getActivity() instanceof Screen) {
            Screen host = (Screen) this.getActivity();
            return host.getViewContext();
        } else {
            return (ViewContext) this.getActivity();
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
