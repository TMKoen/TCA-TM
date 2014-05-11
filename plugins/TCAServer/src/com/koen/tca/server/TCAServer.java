package com.koen.tca.server;

import com.google.inject.Inject;

/**
 * This class is the starting point for the server application.
 * For the Equinox - OSGI framework, there are two methods necessary:
 * Start () and Stop ()
 * start () is the main method where the application begins.
 * 
 */
public class TCAServer {

	@Inject
	TestServer testServer;

	public void start() {
		try {
			// Start the server, listen for RMI requests. 
			testServer.startServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void stop(){
		try {
			// Stop the server. 
			testServer.stopServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
