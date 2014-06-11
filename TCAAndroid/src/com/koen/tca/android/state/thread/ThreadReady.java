package com.koen.tca.android.state.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.android.state.AndroidEvents;
import com.koen.tca.android.wrapper.ReadyDataWrapper;

import android.os.Handler;

public class ThreadReady implements Runnable {

	private Thread readyThread;
	private Handler mainHandler;
	private boolean stopThread;
	private ServerSocket androidServerSocket;
	private int timeout = 1000*5;
	
	final String threadName = "Ready thread";
	public ThreadReady () {
		readyThread = null;
		mainHandler = null;
		stopThread= true;
	}
	
	
	public synchronized void startThread (Handler mainActivityHandler) {
		if (readyThread == null) {
			mainHandler = mainActivityHandler;
			readyThread = new Thread (this, threadName);
			stopThread = false;
			readyThread.start();
		}
	}
	
	public synchronized void stopThread () {
		// Stops the thread
		stopThread = true;
		readyThread = null;
		mainHandler = null;
	}
	
	@Override
	public void run() {
		ReadyDataWrapper dataWrapper = new ReadyDataWrapper ();
		
		DeviceIdentifier device = DeviceIdentifier.SINGLETON();
		if (device.getServerIpAddress()!= null && device.getSocketPortNumber() != 0) {

			try {
				androidServerSocket = new ServerSocket (device.getSocketPortNumber());	
				// Set the timeout how long the server socket accept () method must hold.
				androidServerSocket.setSoTimeout(timeout);
			} catch (IOException e) {
				e.printStackTrace();
				// go out this run() method.
				return;
			}
			while (stopThread != true) {
				try {
					// waits for a command from the Server or a timeout occurs.
					Socket clientSocket = androidServerSocket.accept();
					AndroidEvents event = dataWrapper.receiveEvent(clientSocket.getInputStream());
					
					
				} catch  (IOException e) {
					e.printStackTrace();
				}
					
			}
		}

	}	// method run ()
}	// class ThreadReady
		
