package com.koen.tca.android;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ThreadExpose extends RemoteConnect {

	private Thread androidThread;
	private final String androidThreadName = "Expose thread";
	private boolean stopThread;
	
	private Socket clientSocket;

	
	public ThreadExpose (String serverName, int portNumber) {
		super (serverName, portNumber);

		androidThread = null;
		clientSocket = null;

	}
	
	public synchronized void startThread () {
		if (androidThread == null) {
			stopThread = false;
			androidThread = new Thread (this, androidThreadName);
		}
	}
	
	public synchronized void stopThread () {

		if (androidThread != null) {
			stopThread = true;

			/**
			 * After stopThread = true, the thread is not directly stopped (because of cleaning the thread).
			 * androidThread = null, is safe, because a reference to the thread is hold by the JVM, until it is totally stopped.
			 */
			androidThread = null;
		}
	}

	@Override
	public void run() {

		// Runs the tread until the variable stopThread = true
		while (stopThread != true) {
			
			
			try {
				clientSocket = new Socket (getServerName(), getPortNumber());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
