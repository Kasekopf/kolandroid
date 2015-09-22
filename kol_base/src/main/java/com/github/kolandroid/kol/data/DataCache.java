package com.github.kolandroid.kol.data;

public interface DataCache<A, B> {
    B find(A input);

    void store(B data);
}
