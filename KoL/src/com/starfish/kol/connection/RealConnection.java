package com.starfish.kol.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class RealConnection implements Connection {
	private static final String AGENT_NAME = "AndroidKOL";
	private static final int TIMEOUT = 10000;

	private String URLbase;

	private boolean redirect = true;
	private final ArrayList<String> formFields;

	public RealConnection(String url) {
		this.URLbase = url;
		formFields = new ArrayList<String>();
	}

	public void addFormField(String element, String value) {
		Iterator<String> i = formFields.iterator();
		while (i.hasNext())
			if (i.next().startsWith(element + '='))
				i.remove();
		formFields.add(element + '=' + ((value == null) ? "" : value));
	}

	private URL getFullURL() throws MalformedURLException {
		if (formFields.size() == 0)
			return new URL(URLbase);

		StringBuilder fieldValues = new StringBuilder(URLbase).append('?')
				.append(formFields.get(0));
		for (int i = 1; i < formFields.size(); i++)
			fieldValues.append('&').append(formFields.get(i));
		return new URL(fieldValues.toString());
	}

	public void disableRedirects() {
		redirect = false;
	}

	public ServerReply complete(String cookie) throws ConnectionException {
		try {
			HttpURLConnection connection = this.connect(cookie);
			return new ServerReply(connection);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}

	private HttpURLConnection connect(String cookie) throws IOException {
		URL url = getFullURL();

		System.out.println("Making request to " + url);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		if (cookie != null)
			connection.setRequestProperty("Cookie", cookie);
		connection.setDoInput(true);
		connection.setRequestProperty("User-Agent", AGENT_NAME);
		connection.setReadTimeout(TIMEOUT);
		connection.setInstanceFollowRedirects(redirect);
		connection.setUseCaches(true);
		connection.setDoOutput(false);
		connection.connect();
		return connection;
	}

	@Override
	public String getUrl() {
		return URLbase;
	}
}
