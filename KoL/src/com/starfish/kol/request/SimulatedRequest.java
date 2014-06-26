package com.starfish.kol.request;

import com.starfish.kol.connection.Session;
import com.starfish.kol.connection.Connection.ServerReply;

public class SimulatedRequest extends Request{
	private ServerReply toReply;
	
	public SimulatedRequest(ServerReply toReply) {
		this(toReply, ResponseHandler.none);
	}
	
	public SimulatedRequest(ServerReply prototype, String newurl, String newhtml) {
		this(new ServerReply(prototype.responseCode, prototype.redirectLocation, prototype.date, newhtml, newurl, prototype.cookie));
	}
	
	public SimulatedRequest(ServerReply toReply, ResponseHandler handler) {
		super(null, handler);
		
		this.toReply = toReply;
	}

	/**
	 * Pretend to make the request and return a response.
	 * Only returns the ServerReply provided at construction.
	 *  
	 * @param session	Session passed to the response handler.
	 * @param server	ignored
	 * @param cookie	ignored
	 * @return	An unhandled ServerReply. Null if the reply has already been handled.
	 */
	public ServerReply make(Session session, String server, String cookie) {
		if(this.getHandler().handle(session, this, toReply))
			return null;
		return toReply;
	}
}
