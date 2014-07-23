package com.starfish.kol.request;

import java.net.MalformedURLException;
import java.util.HashSet;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.ConnectionException;
import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;

public class Request {
	private String url;
	private ResponseHandler handler;

	private HashSet<String> tags;

	public Request(String url, ResponseHandler handler) {
		this.url = url;
		this.handler = handler;

		this.tags = new HashSet<String>();
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}

	public void makeAsync(final Session session) {
		Thread t = new Thread() {
			public void run() {
				make(session, session.getServer(), session.getCookie());
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
	protected void make(Session session, String server, String cookie) {
		try {
			Connection con = getConnection(server);
			ServerReply response = con.connect(cookie);
			getHandler().handle(session, this, response);
		} catch (MalformedURLException | ConnectionException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}

	protected Connection getConnection(String server) {
		return new Connection("http://" + server + ".kingdomofloathing.com/"
				+ url);
	}

	protected ResponseHandler getHandler() {
		return handler;
	}

	public String toString() {
		return "$Request[" + url + "]";
	}
}
