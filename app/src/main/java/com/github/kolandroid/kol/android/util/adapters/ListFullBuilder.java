package com.github.kolandroid.kol.android.util.adapters;

import android.view.View;

import com.github.kolandroid.kol.model.elements.interfaces.ModelGroup;

public interface ListFullBuilder<E extends ModelGroup<F>, F> extends ListElementBuilder<F> {
    int getGroupLayout();

    int getChildLayout();

    void fillGroup(View view, E group);

    void fillChild(View view, F child);
}
