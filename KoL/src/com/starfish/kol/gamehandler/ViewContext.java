package com.starfish.kol.gamehandler;

import com.starfish.kol.model.Model;

/**
 * A representation of the current view context. This interface can create new
 * views in the same context when given a model to display.
 */
public interface ViewContext {
	/**
	 * Display the provided model.
	 * 
	 * @param model
	 *            The model to display.
	 */
	public <E extends Model<?>> void display(E model);
}
