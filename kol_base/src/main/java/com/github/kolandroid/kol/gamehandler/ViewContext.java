package com.github.kolandroid.kol.gamehandler;

import com.github.kolandroid.kol.request.ResponseHandler;

/**
 * A representation of the current view context. This interface can create new
 * views in the same context when given a model to display.
 */
public interface ViewContext {
    /**
     * Get a handler to the primary route.
     *
     * @return The primary route.
     */
    ResponseHandler getPrimaryRoute();

    /*
     * Provide a context to display loading information.
     *
     * @returns A reference to a new progress indicator.
     */
    LoadingContext createLoadingContext();

    /*
     * Provide a context for accessing game data.
     *
     * @returns A reference to a new context for game data.
     */
    DataContext getDataContext();
}
