package com.starfish.kol.request;

import com.starfish.kol.connection.Connection.ServerReply;
import com.starfish.kol.connection.Session;

public class DirectRequest extends Request {
	public DirectRequest(String url, ResponseHandler handler) {
		super(url, handler);
	}

	public ServerReply make(Session session, String server, String cookie) {
		ServerReply response = super.make(session, server, cookie);
		if(response != null) //response was not actually handled by request
			System.err.println("Handler unable to handle DirectRequest: "+ response.url);
		return null;
	}
}
