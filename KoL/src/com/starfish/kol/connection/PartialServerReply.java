package com.starfish.kol.connection;

import java.io.IOException;
import java.net.HttpURLConnection;

public class PartialServerReply {
	public final int responseCode;
	public final String redirectLocation;
	public final String date;
	public final String url;
	public final String cookie;

	private transient HttpURLConnection base;
	
	private ServerReply simulatedBase;
	
	public PartialServerReply(ServerReply simulatedBase) {
		this.responseCode = simulatedBase.responseCode;
		this.redirectLocation = simulatedBase.redirectLocation;
		this.date = simulatedBase.date;
		this.url = simulatedBase.url;
		this.cookie = simulatedBase.cookie;
		
		this.simulatedBase = simulatedBase;
	}
	
	public PartialServerReply(HttpURLConnection base) throws IOException {
		responseCode = base.getResponseCode();
		redirectLocation = (responseCode / 100 == 3) ? base
				.getHeaderField("Location") : null;

		url = base.getURL().toString();
		date = base.getHeaderField("Date");
		cookie = getCookie(base);
		this.base = base;
	}

	private String getCookie(HttpURLConnection base) {
		String cookies = "";
		for (int i = 0;; i++) {
			if (base.getHeaderField(i) == null)
				break;

			if ("Set-Cookie".equals(base.getHeaderFieldKey(i))) {
				if (cookies.length() > 0)
					cookies += "; ";
				cookies += base.getHeaderField(i);
			}
		}
		return cookies;
	}

	public ServerReply complete() {
		if(simulatedBase != null)
			return simulatedBase;
		
		try {
			return new ServerReply(this, base);
		} catch (IOException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		String s = "RESPONSE CODE: " + responseCode;
		if (responseCode / 100 == 3)
			s += "\nREDIRECT: " + redirectLocation;
		s += "\nRESULT: [UNLOADED]";
		return s;
	}
}
