package com.github.kolandroid.kol.android.util.adapters;

import android.view.View;

import java.io.Serializable;

public interface ListElementBuilder<F> extends Serializable {
    int getChildLayout();

    void fillChild(View view, F child);
}
