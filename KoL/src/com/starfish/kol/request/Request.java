package com.starfish.kol.request;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.ConnectionException;
import com.starfish.kol.connection.RealConnection;
import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.LoadingContext;

public class Request {
	private String url;

	public Request(String url) {
		this.url = url;
	}

	public void makeAsync(final Session session, final LoadingContext loading, final ResponseHandler handler) {
		Thread t = new Thread() {
			public void run() {
				make(session, loading, handler);
			}
		};
		t.start();
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
	protected void make(Session session, LoadingContext loading, ResponseHandler handler) {
		Connection con = getConnection(session.getServer());
		String url = con.getUrl();
		loading.start(con.getUrl());
		try {
			ServerReply reply = con.complete(session.getCookie());
			loading.complete(url);
			handler.handle(session, this, reply);
		} catch (ConnectionException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
			loading.error(url);
		}
	}

	protected Connection getConnection(String server) {
		return new RealConnection("http://" + server
				+ ".kingdomofloathing.com/" + url);
	}


	public String toString() {
		return "$Request[" + url + "]";
	}
}
