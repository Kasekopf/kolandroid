package com.github.kolandroid.kol.android.screen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.gamehandler.ViewContext;

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
        // do nothing
    }
}
