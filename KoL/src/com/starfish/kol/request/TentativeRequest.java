package com.starfish.kol.request;

import java.net.MalformedURLException;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.ConnectionException;
import com.starfish.kol.connection.ServerReply;
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
			ServerReply response = con.connect(cookie);
			boolean done = getHandler().handle(session, this, response);
			if (!done)
				throw new RuntimeException("No valid handler for: " + this.toString());
		} catch (MalformedURLException | ConnectionException e) {
			failure.handle(session, this, null);
		}
	}
}
