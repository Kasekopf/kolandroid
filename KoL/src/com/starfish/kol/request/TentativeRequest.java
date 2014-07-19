package com.starfish.kol.request;

import java.net.MalformedURLException;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.Connection.ConnectionException;
import com.starfish.kol.connection.Connection.ServerReply;
import com.starfish.kol.connection.Session;

public class TentativeRequest extends Request {	
	public TentativeRequest(String url, ResponseHandler handler) {
		super(url, handler);
	}

	@Override
	public void make(Session session, String server, String cookie) {
		try {
			Connection con = new Connection("http://" + server
					+ ".kingdomofloathing.com/" + getURL());
			this.prepare(con);

			ServerReply response = con.connect(cookie);
			boolean done = getHandler().handle(session, this, response);
			if (!done)
				throw new RuntimeException("No valid handler for: " + this.toString());
		} catch (MalformedURLException | ConnectionException e) {
			getHandler().handle(session, this, null);
		}
	}
}
