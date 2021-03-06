package com.github.kolandroid.kol.android.controller;

import android.view.View;

import com.github.kolandroid.kol.android.binders.Binder;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;

public class BinderController<E> implements Controller {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -8821670180432097129L;

    private final Binder<? super E> binder;
    private E displayed;

    private transient View view;

    public BinderController(Binder<? super E> binder, E defaultElem) {
        this.binder = binder;
        this.displayed = defaultElem;
    }

    @Override
    public int getView() {
        return binder.getView();
    }

    public E getValue() {
        return displayed;
    }

    public void updateModel(E toDisplay) {
        this.displayed = toDisplay;
        if (view != null)
            binder.bind(view, toDisplay);
    }

    @Override
    public void attach(View view, Screen host) {
        this.view = view;
        binder.bind(view, displayed);
    }

    @Override
    public void connect(View view, Screen host) {
        // Do nothing
    }

    @Override
    public void disconnect(Screen host) {
        view = null;
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }
}
