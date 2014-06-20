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

	// Injection of the TestServer object
	@Inject
	TestServer testServer;

	/**
	 * The starting point where the Server starts.
	 * <p>
	 * This the main method where 
	 * @param
	 * @return
	 * @throws Exception
	 * @version
	 * @author Koen Nijmeijer
	 * @see TestServer
	 * 
	 */
	public void start() {
		try {
			// Start the server, listen for RMI requests.
			// The
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

	public TestServer getTestServer() {
		return testServer;
	}

}
