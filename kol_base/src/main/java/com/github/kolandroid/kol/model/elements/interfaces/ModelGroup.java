package com.github.kolandroid.kol.model.elements.interfaces;

import java.io.Serializable;

public interface ModelGroup<E> extends Serializable, Iterable<E> {
    int size();

    E get(int index);

    void set(int index, E value);

    void remove(int index);

    String getName();
}
