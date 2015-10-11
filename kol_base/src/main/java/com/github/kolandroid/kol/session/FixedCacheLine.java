package com.github.kolandroid.kol.session;

import java.io.Serializable;

/**
 * A single cache line which holds a single item.
 * This item cannot possibly change over the course of the session once it is computed
 * successfully, and so ignores further dirty marks.
 *
 * @param <E> The type of the item inside the cache.
 */
public abstract class FixedCacheLine<E extends Serializable> extends CacheLine<E> {
    @Override
    void markDirty() {
        // Do nothing
    }
}
