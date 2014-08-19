package com.starfish.kol.request;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.LoadingContext;

public class SimulatedRequest extends Request{
	private ServerReply toReply;
		
	public SimulatedRequest(ServerReply prototype, String newurl, String newhtml) {
		this(new ServerReply(prototype.responseCode, prototype.redirectLocation, prototype.date, newhtml, newurl, prototype.cookie));
	}
	
	public SimulatedRequest(ServerReply toReply) {
		super(toReply.url);
		
		this.toReply = toReply;
	}
	
	/**
	 * Actually make the request and return a response. This should be run on a
	 * background thread only!
	 * 
	 * @param session
	 *            Session passed to the response handler.
	 * @param server
	 *            Server to use for this request
	 * @param cookie
	 *            Cookie to use with this request
	 */
	@Override
	protected void make(Session session, LoadingContext loading, ResponseHandler handler) {
		loading.start(toReply.url);
		loading.complete(toReply.url);
		handler.handle(session, toReply);
	}
}
