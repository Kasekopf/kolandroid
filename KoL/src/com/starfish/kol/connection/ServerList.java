package com.starfish.kol.connection;



public class ServerList {
	private static final String[][] SERVERS = {{ "http://www.kingdomofloathing.com", "69.16.150.196" },
		{ "http://www2.kingdomofloathing.com", "69.16.150.197" },
		{ "http://www3.kingdomofloathing.com", "69.16.150.198" },
		{ "http://www4.kingdomofloathing.com", "69.16.150.199" },
		{ "http://www5.kingdomofloathing.com", "69.16.150.200" },
		{ "http://www6.kingdomofloathing.com", "69.16.150.205" },
		{ "http://www7.kingdomofloathing.com", "69.16.150.206" }};
	
	public static String getName(int n) {
		return SERVERS[n][0];
	}

	public static String getIP(int n) {
		return SERVERS[n][1];
	}

	public static int numServers() {
		return SERVERS.length;
	}
}
