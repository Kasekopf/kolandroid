package com.github.kolandroid.kol.session.cache;

import com.github.kolandroid.kol.util.Callback;

import java.io.Serializable;

/**
 * A single cache line which holds a single item. This item will NOT be recomputed if needed
 * and must be manually refilled after being dirtied.
 *
 * @param <E> The type of the item inside the cache.
 */
public class BasicCacheLine<E extends Serializable> extends CacheLine<E> {
    /**
     * Create a new cache line, preloaded with the provided item.
     *
     * @param def The initial item inside the cache
     */
    public BasicCacheLine(E def) {
        this.fill(def);
    }

    /**
     * Immediately fail to recompute the item.
     * @param cache     Cache which provides any relevant dependencies; ignored
     * @param complete  Callback to call when the stored item is recomputed; ignored
     * @param failure   Callback to call when we are unable to compute this item
     */
    @Override
    protected void recompute(SessionCache cache, Callback<E> complete, Callback<Void> failure) {
        failure.execute(null);
    }

    /**
     * No dependencies, since we cannot recompute the item.
     * @return []
     */
    @Override
    protected Class[] dependencies() {
        return new Class[0];
    }
}
