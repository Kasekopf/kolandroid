package com.starfish.kol.request;

import com.starfish.kol.connection.Connection;

public class SingleRequest extends Request{
	private final String[] formNames;
	private final String[] formVals;
	
	public SingleRequest(String url, String[] names, String[] vals, ResponseHandler handler) {
		super(url, handler);
		
		this.formNames = names;
		this.formVals = vals;
	}

	@Override
	protected Connection getConnection(String server) {
		Connection connection = super.getConnection(server);

		for (int i = 0; i < formNames.length; i++) {
			connection.addFormField(formNames[i], formVals[i]);
		}
		connection.disableRedirects();
		return connection;
	}
}
