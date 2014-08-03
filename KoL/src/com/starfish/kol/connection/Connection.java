package com.starfish.kol.connection;

public interface Connection {
	public PartialServerReply complete(String cookie) throws ConnectionException;
	public void addFormField(String element, String value);
	public void disableRedirects();
}
