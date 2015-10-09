package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class CacheItem<E> implements Serializable {
    private E storage;
    private List<CacheItem> dependents = new ArrayList<>();
    private boolean dirty = true;

    void access(SessionCache cache, final Callback<E> callback, final Callback<Void> failure) {
        if (storage != null && !dirty) {
            callback.execute(storage);
        }

        recompute(cache, new Callback<E>() {
            @Override
            public void execute(E item) {
                if (item == null) {
                    failure.execute(null);
                } else {
                    fill(item);
                    callback.execute(item);
                }
            }
        }, failure);
    }

    void fill(E item) {
        this.dirty = false;

        if (storage == null || !storage.equals(item)) {
            if (item != null) {
                Logger.log("CacheItem", item.getClass().getSimpleName() + " computed in cache as " + item);
            }

            for (CacheItem dependent : dependents) {
                dependent.markDirty();
            }
            this.storage = item;
        }


    }

    private void markDirty() {
        if (this.dirty) return;

        this.dirty = true;
        for (CacheItem item : dependents) {
            item.markDirty();
        }
    }

    abstract void recompute(SessionCache cache, Callback<E> complete, Callback<Void> failure);

    abstract Class[] dependencies();

    <F> void addDependent(CacheItem<F> dependent) {
        dependents.add(dependent);
    }
}
