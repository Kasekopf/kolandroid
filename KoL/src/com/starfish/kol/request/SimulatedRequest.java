package com.starfish.kol.request;

import com.starfish.kol.connection.Connection;
import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.SimulatedConnection;

public class SimulatedRequest extends Request{
	private ServerReply toReply;
		
	public SimulatedRequest(ServerReply prototype, String newurl, String newhtml) {
		this(new ServerReply(prototype.responseCode, prototype.redirectLocation, prototype.date, newhtml, newurl, prototype.cookie));
	}
	
	public SimulatedRequest(ServerReply toReply) {
		super(toReply.url);
		
		this.toReply = toReply;
	}
	
	protected Connection getConnection(String server) {
		return new SimulatedConnection(toReply);
	}
}
