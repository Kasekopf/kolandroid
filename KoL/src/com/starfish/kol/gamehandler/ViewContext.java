package com.starfish.kol.gamehandler;

import com.starfish.kol.request.ResponseHandler;

/**
 * A representation of the current view context. This interface can create new
 * views in the same context when given a model to display.
 */
public interface ViewContext {
	/**
	 * Get a handler to the primary route.
	 * 
	 * @return
	 * 		The primary route.
	 */
	public ResponseHandler getPrimaryRoute();
	
	/*
	 * Provide a context to display loading information.
	 * 
	 * @returns A reference to a new progress indicator.
	 */
	public LoadingContext createLoadingContext();
	
	/*
	 * Provide a context for accessing game data.
	 * 
	 * @returns A reference to a new context for game data.
	 */
	public DataContext getDataContext();
}
