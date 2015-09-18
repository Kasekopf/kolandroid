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
     * @return A reference to a new progress indicator.
     */
    LoadingContext createLoadingContext();

    /*
     * Provide a context for accessing game data.
     *
     * @return A reference to a new context for game data.
     */
    DataContext getDataContext();

    /*
     * Display a brief message to the user.
     *
     * @param message   The message to display.
     */
    void displayMessage(String message);

    /*
     * Provide a context for accessing app settings.
     *
     * @return A reference to a new context for accessing app settings.
     */
    SettingsContext getSettingsContext();
}
