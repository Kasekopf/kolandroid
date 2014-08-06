package com.starfish.kol.request;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.ConnectionException;
import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.LoadingContext;

public class TentativeRequest extends Request {
	private ResponseHandler failure;

	public TentativeRequest(String url, ResponseHandler success,
			ResponseHandler failure) {
		super(url, success);

		this.failure = failure;
	}

	@Override
	public void make(Session session, LoadingContext loading, String server, String cookie) {
		Connection con = getConnection(server);
		String url = con.getUrl();
		loading.start(con.getUrl());
		try {
			ServerReply reply = con.complete(cookie);
			loading.complete(url);
			getHandler().handle(session, this, reply);
		} catch (ConnectionException e) {
			loading.error(url);
			failure.handle(session, this, null);
		}
	}
}
