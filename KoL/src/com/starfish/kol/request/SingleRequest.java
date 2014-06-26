package com.starfish.kol.request;

import com.starfish.kol.connection.Connection;

public class SingleRequest extends Request{

	public SingleRequest(String url, ResponseHandler handler) {
		super(url, handler);
	}

	public SingleRequest(String url, String[] names, String[] vals, ResponseHandler handler) {
		super(url, names, vals, handler);
	}

	public void prepare(Connection c) {
		super.prepare(c);
		c.disableRedirects();
	}
}
