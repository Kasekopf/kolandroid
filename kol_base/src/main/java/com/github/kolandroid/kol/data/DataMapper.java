package com.github.kolandroid.kol.data;

public interface DataMapper<A, B> {
    A process(B input);
}
