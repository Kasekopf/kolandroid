package com.starfish.kol.request;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.ConnectionException;
import com.starfish.kol.connection.PartialServerReply;
import com.starfish.kol.connection.Session;

public class TentativeRequest extends Request {	
	private ResponseHandler failure;
	
	public TentativeRequest(String url, ResponseHandler success, ResponseHandler failure) {
		super(url, success);
	
		this.failure = failure;
	}

	@Override
	public void make(Session session, String server, String cookie) {
		try {
			Connection con = getConnection(server);
			PartialServerReply reply = con.complete(cookie);
			getHandler().handle(session, this, reply.complete());
		} catch (ConnectionException e) {
			failure.handle(session, this, null);
		}
	}
}
