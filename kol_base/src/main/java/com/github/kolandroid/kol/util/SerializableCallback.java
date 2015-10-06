package com.github.kolandroid.kol.util;

import java.io.Serializable;

/**
 * A simple serializable interface for a callback, since this is Java 1.7 so there are no spiffy lambdas.
 *
 * @param <Args> Type of the value passed to the callback
 */
public interface SerializableCallback<Args> extends Callback<Args>, Serializable {

}
