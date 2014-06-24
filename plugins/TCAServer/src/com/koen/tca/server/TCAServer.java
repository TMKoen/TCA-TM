package com.koen.tca.server;

import com.google.inject.Inject;

/**
 * This class initiates the server instance.
 * <p>
 * @version
 * @author Koen Nijmeijer
 * 
 */
public class TCAServer {

	// Injection of the TestServer object. It Creates a new TestServer object.
	@Inject
	TestServer testServer;

	/**
	 * The starting point where the Server starts.
	 * <p>
	 * This the main method where 
	 * @version
	 * @author Koen Nijmeijer
	 * @param
	 * @return
	 * @throws Exception

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
