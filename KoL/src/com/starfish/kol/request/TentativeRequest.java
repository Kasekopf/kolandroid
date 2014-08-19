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
		super(url);

		this.failure = failure;
	}

	@Override
	protected void make(Session session, LoadingContext loading, ResponseHandler handler) {
		Connection con = getConnection(session.getServer());
		String url = con.getUrl();
		loading.start(con.getUrl());
		try {
			ServerReply reply = con.complete(session.getCookie());
			loading.complete(url);
			handler.handle(session, this, reply);
		} catch (ConnectionException e) {
			loading.error(url);
			failure.handle(session, this, null);
		}
	}
}
