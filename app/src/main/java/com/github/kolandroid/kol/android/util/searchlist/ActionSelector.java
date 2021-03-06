package com.github.kolandroid.kol.android.util.searchlist;

import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.model.elements.ActionElement;

import java.io.Serializable;

public class ActionSelector<E extends ActionElement> implements ListSelector<E>, Serializable {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 3463753434332L;

    @Override
    public boolean selectItem(Screen list, E item) {
        item.submit(list.getViewContext());
        return true;
    }
}