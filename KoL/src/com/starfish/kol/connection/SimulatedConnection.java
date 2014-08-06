package com.starfish.kol.connection;

public class SimulatedConnection implements Connection {
	private ServerReply base;
	
	public SimulatedConnection(ServerReply base) {
		this.base = base;
	}

	@Override
	public void addFormField(String element, String value) {
		//do nothing
	}

	@Override
	public ServerReply complete(String cookie) throws ConnectionException {
		return base;
	}

	@Override
	public void disableRedirects() {
		//do nothing
	}

	@Override
	public String getUrl() {
		return base.url;
	}
}
