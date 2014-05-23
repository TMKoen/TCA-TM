package com.koen.tca.android;

public class RemoteConnect implements Runnable{

	private String serverName;
	private int portNumber;
	
	public RemoteConnect (String serverName, int portNumber) {
		setServerName (serverName);
		setPortNumber (portNumber);
	}
	
	public void setServerName (String serverName) {
		this.serverName = serverName;
	}
	
	public void setPortNumber (int portNumber) {

		if (portNumber >= 1024 && portNumber < 65536) {
			this.portNumber = portNumber;
		}
		this.portNumber = portNumber;
	}
	
	public String getServerName () {
		return serverName;
	}
	
	public int getPortNumber () {
		return portNumber;
	}

	@Override
	public void run() {
		
	}
}
