package com.github.kolandroid.kol.util;

/**
 * A simple interface for a callback, since this is Java 1.7 so there are no spiffy lambdas.
 *
 * @param <Args> Type of the value passed to the callback
 */
public interface Callback<Args> {
    /**
     * Execute the callback with the provided arguments.
     *
     * @param item Arguments to the callback.
     */
    void execute(Args item);
}
