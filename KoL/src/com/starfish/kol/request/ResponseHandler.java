package com.starfish.kol.request;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;

public interface ResponseHandler {
	public boolean handle(Session session, Request request, ServerReply response);
	
	public static ResponseHandler none = new ResponseHandler() {
		@Override
		public boolean handle(Session session, Request request,
				ServerReply response) {
			return false;
		}
	};
}
