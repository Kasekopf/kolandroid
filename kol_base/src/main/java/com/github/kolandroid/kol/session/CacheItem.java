package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.util.Callback;

import java.io.Serializable;

public interface CacheItem<E> extends Serializable {
    void access(Callback<E> callback, Callback<Void> failure);

    void fill(E content);
}
