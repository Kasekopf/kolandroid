package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.util.Callback;

import java.io.Serializable;

public class BasicCacheItem<E extends Serializable> extends CacheItem<E> {
    private E storage = null;

    public BasicCacheItem(E def) {
        this.fill(def);
    }

    @Override
    void recompute(SessionCache cache, Callback<E> complete, Callback<Void> failure) {
        failure.execute(null);
    }

    @Override
    Class[] dependencies() {
        return new Class[0];
    }
}
