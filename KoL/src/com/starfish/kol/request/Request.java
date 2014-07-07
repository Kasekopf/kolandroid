package com.starfish.kol.request;

import java.net.MalformedURLException;
import java.util.HashSet;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.Connection.ConnectionException;
import com.starfish.kol.connection.Connection.ServerReply;
import com.starfish.kol.connection.Session;

public class Request {
	private String url;
	private ResponseHandler handler;

	private HashSet<String> tags;
	
	private String[] formNames;
	private String[] formVals;

	public Request(String url) {
		this(url, ResponseHandler.none);
	}
	
	public Request(String url, ResponseHandler handler) {
		this(url, new String[0], new String[0], handler);
	}

	 protected Request(String url, String[] names, String[] vals,
			ResponseHandler handler) {
		this.url = url;
		this.handler = handler;
		this.formNames = names;
		this.formVals = vals;
		
		this.tags = new HashSet<String>();
	}

	public void addTag(String tag) {
		tags.add(tag);
	}
	
	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}
	/**
	 * Actually make the request and return a response.
	 * This should be run on a background thread only!
	 *  
	 * @param session	Session passed to the response handler.
	 * @param server	Server to use for this request
	 * @param cookie	Cookie to use with this request
	 * @return	An unhandled ServerReply. Null if the reply has already been handled.
	 */
	public ServerReply make(Session session, String server, String cookie) {
		try {
			Connection con = new Connection("http://" + server
					+ ".kingdomofloathing.com/" + getURL());
			this.prepare(con);

			System.out.println("Making request to " + getURL());
			ServerReply response = con.connect(cookie);
			boolean done = getHandler().handle(session, this, response);
			if (!done)
				return response;
			return null;
		} catch (MalformedURLException | ConnectionException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
			return null;
		}
	}

	public String getURL() {
		return url;
	}

	public void prepare(Connection c) {
		for (int i = 0; i < formNames.length; i++) {
			c.addFormField(formNames[i], formVals[i]);
		}
	}

	public ResponseHandler getHandler() {
		return handler;
	}
}
