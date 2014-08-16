package com.starfish.kol.gamehandler;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.request.Request;
import com.starfish.kol.request.ResponseHandler;

/**
 * The standard ResponseHandler for most of the app, used to display an
 *  arbitrary game page with the appropriate view and model.
 *
 */
public class GameHandler implements ResponseHandler {
	//Context used to map models to views.
	private ViewContext view;
	
	/**
	 * Create a new ResponseHandler which displays arbitrary game pages
	 *  on the provided view context.
	 *  
	 * @param view	Context to display new models.
	 */
	public GameHandler(ViewContext view) {
		this.view = view;
	}

	/**
	 * Process a new reply from the server. In this case, display this response
	 *  in a newly generated view.
	 * 
	 * @param session
	 *            The session used when making the request.
	 * @param request
	 *            The request which generated this reply.
	 * @param response
	 *            The response recieved from the server.
	 */
	@Override
	public void handle(Session session, Request request, ServerReply response) {
		ResponseHandler route = view.getPrimaryRoute();
		route.handle(session, request, response);
	}
}
