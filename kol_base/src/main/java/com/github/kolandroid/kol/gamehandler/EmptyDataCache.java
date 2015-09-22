package com.github.kolandroid.kol.gamehandler;

import com.github.kolandroid.kol.data.DataCache;

public class EmptyDataCache<E, F> implements DataCache<E, F> {
    @Override
    public F find(E input) {
        return null;
    }

    @Override
    public void store(F data) {
        // Do nothing;
    }
}
