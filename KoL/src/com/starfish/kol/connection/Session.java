package com.starfish.kol.connection;

import com.starfish.kol.connection.Connection.ServerReply;
import com.starfish.kol.request.Request;
import com.starfish.kol.request.ResponseHandler;

public class Session {
	private ResponseHandler globalHandler;

	private String server = "www";
	private String cookie = null;

	public Session(ResponseHandler globalHandler) {
		this.globalHandler = globalHandler;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void handle(final Request request) {
		Thread t = new Thread() {
			public void run() {
				ServerReply response = request.make(Session.this, server,
						cookie);
				if (response != null) {
					boolean done = globalHandler.handle(Session.this, request,
							response);
					if (!done) {
						throw new RuntimeException(
								"Global Response handler was unable to handle request "
										+ request.toString());
					}
				}
			}
		};
		t.start();

	}
}
