package com.koen.tca.server;

/**
 * Server side main method.
 */
public class TCAServer {

	public static void main(String[] args) {
		try {
			ServerRMI serverRMI = new ServerRMI();

			// Start the server.
			serverRMI.startServer();

			// Sleep for 5 minutes.
			Thread.sleep(5 * 60 * 1000);

			// Stopping the server.
			serverRMI.stopServer();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
