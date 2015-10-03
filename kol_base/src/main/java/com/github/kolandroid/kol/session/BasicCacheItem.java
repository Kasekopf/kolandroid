package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.util.Callback;

import java.io.Serializable;

public class BasicCacheItem<E extends Serializable> implements CacheItem<E> {
    private E storage = null;

    public BasicCacheItem() {
        this.storage = null;
    }

    public BasicCacheItem(E def) {
        this.storage = def;
    }

    @Override
    public void access(Callback<E> callback, Callback<Void> failure) {
        if (storage != null) {
            callback.execute(storage);
        } else {
            failure.execute(null);
        }
    }

    @Override
    public void fill(E content) {
        this.storage = content;
    }
}
