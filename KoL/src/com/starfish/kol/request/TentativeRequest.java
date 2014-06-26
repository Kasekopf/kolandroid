package com.starfish.kol.request;

import java.net.MalformedURLException;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.Session;
import com.starfish.kol.connection.Connection.ConnectionException;
import com.starfish.kol.connection.Connection.ServerReply;

public class TentativeRequest extends Request {
	public TentativeRequest(String url, ResponseHandler handler) {
		super(url, handler);
	}

	public TentativeRequest(String url, String[] names, String[] vals,
			ResponseHandler handler) {
		super(url, names, vals, handler);
	}

	@Override
	public ServerReply make(Session session, String server, String cookie) {
		try {
			Connection con = new Connection("http://" + server
					+ ".kingdomofloathing.com/" + getURL());
			this.prepare(con);

			ServerReply response = con.connect(cookie);
			boolean done = getHandler().handle(session, this, response);
			if (!done)
				return response;
			return null;
		} catch (MalformedURLException | ConnectionException e) {
			getHandler().handle(session, this, null);
			return null;
		}
	}
}
