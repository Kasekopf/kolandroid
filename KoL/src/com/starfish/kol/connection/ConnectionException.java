package com.starfish.kol.connection;

import java.io.IOException;

public class ConnectionException extends IOException {
	private static final long serialVersionUID = 1592480171367152698L;
	
	private Exception base;
	
	public ConnectionException(Exception base) {
		this.base = base;
	}
			
	@Override
	public String toString() {
		return base.toString();
	}
}