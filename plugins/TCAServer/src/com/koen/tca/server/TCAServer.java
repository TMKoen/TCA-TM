package com.koen.tca.server;

import com.google.inject.Inject;

/**
 * Server side main method.
 */
public class TCAServer {

	@Inject
	RmiServerInterface serverRMI;

	public void start() {
		try {
			// Start the server, listen for RMI requests. 
			serverRMI.startServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void stop(){
		try {
			// Stop the server. 
			serverRMI.stopServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
