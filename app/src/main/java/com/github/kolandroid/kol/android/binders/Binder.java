package com.github.kolandroid.kol.android.binders;

import android.view.View;

public interface Binder<E> {
    int getView();

    void bind(View view, E model);
}