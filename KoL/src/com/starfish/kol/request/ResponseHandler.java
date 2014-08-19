package com.starfish.kol.request;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;

/**
 * A callback used to process server responses to game requests.
 */
public interface ResponseHandler {
	/**
	 * Process a new reply from the server.
	 * 
	 * @param session
	 *            The session used when making the request.
	 * @param response
	 *            The response recieved from the server.
	 */
	public void handle(Session session, ServerReply response);
}
