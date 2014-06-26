package com.starfish.kol.connection;

import java.util.Random;

public class ConnectionInfo {
	private String cookie = null;
	private int server = -1;
	private String serverName;
	private Random r = new Random();
	private boolean[] attemptedServers = new boolean[ServerList.numServers()];
	
	public ConnectionInfo() throws ConnectionException{
		pickNewServer();
	}
	
	public ConnectionInfo(String cookie, int server){
		this.cookie = cookie;
		this.server = server;
	}

	@SuppressWarnings("all") 
	public void pickNewServer() throws ConnectionException{
		if(server > -1)
			attemptedServers[server] = true;
		
		int a = r.nextInt(ServerList.numServers());
		for(int i = 0; i < ServerList.numServers(); i++){
			server = (a + i) % ServerList.numServers();
			serverName = ServerList.getName(server);
			if(!attemptedServers[server])
				return;
		}
		
		throw new ConnectionException("Error: Unable to connect to any server");
	}
	
	public void setCookie(String cookie){
		this.cookie = cookie;
	}
	
	public String getCookie(){
		return cookie;
	}
	
	public String getServer(){
		return serverName;
	}
	
	public class ConnectionException extends Exception{
		public ConnectionException(String message){
			super(message);
		}
		private static final long serialVersionUID = 2075667009191051549L;
	}
}
