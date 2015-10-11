package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A single cache line which holds a single item. This item can be recomputed on access
 * if it does not already exist.
 *
 * @param <E> The type of the item inside the cache.
 */
public abstract class CacheLine<E extends Serializable> implements Serializable {
    // Cached version of the item, or null if nothing is stored.
    private E storage;

    // List of all cache items which depend on this item
    private List<CacheLine> dependents = new ArrayList<>();

    // Check if the stored item should be recomputed on next access
    private boolean dirty = true;

    /**
     * Attempt to access the stored item, either using the stored copy or recomputing if dirty.
     * @param cache     Cache which provides any relevant dependencies
     * @param callback  Callback to call when the stored item is located
     * @param failure   Callback to call when we are unable to compute this item
     */
    void access(SessionCache cache, final Callback<E> callback, final Callback<Void> failure) {
        if (storage != null && !dirty) {
            callback.execute(storage);
            return;
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

    /**
     * Explicitly change the stored version of the item
     * @param item  The new version of the item to store
     */
    void fill(E item) {
        // If dirty, the cache has been re-updated
        this.dirty = false;

        // If the new item is genuinely different, all dependents should be recomputed on next access
        if (storage == null || !storage.equals(item)) {
            if (item != null) {
                Logger.log("CacheLine", item.getClass().getSimpleName() + " computed in cache as " + item);
            }

            this.storage = item;
            for (CacheLine dependent : dependents) {
                dependent.markDirty();
            }
        }
    }

    /**
     * Recompute this item when it is next accessed, instead of using the cached copy.
     */
    void markDirty() {
        if (this.dirty) return;

        this.dirty = true;
        for (CacheLine item : dependents) {
            item.markDirty();
        }
    }

    /**
     * Recompute the stored item, possibly making use of the declared dependencies in the cache
     * @param cache     Cache which provides any relevant dependencies
     * @param complete  Callback to call when the stored item is recomputed
     * @param failure   Callback to call when we are unable to compute this item
     */
    protected abstract void recompute(SessionCache cache, Callback<E> complete, Callback<Void> failure);

    /**
     * A list of dependencies for this cached item. If any of these are recomputed, this item
     * should be recomputed on next access.
     * @return
     */
    protected abstract Class[] dependencies();

    /**
     * Add a dependent to this cache item, to be dirtied whenever this item is recomputed.
     *
     * @param dependent The new dependent cache item
     * @param <F>       The type of the new dependent cache item
     */
    <F extends Serializable> void addDependent(CacheLine<F> dependent) {
        dependents.add(dependent);
    }
}
