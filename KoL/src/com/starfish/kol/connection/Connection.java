package com.starfish.kol.connection;

public interface Connection {
	public ServerReply complete(String cookie) throws ConnectionException;
	public void addFormField(String element, String value);
	public void disableRedirects();
	
	public String getUrl();
}
