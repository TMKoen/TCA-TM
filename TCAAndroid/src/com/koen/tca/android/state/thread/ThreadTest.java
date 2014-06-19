package com.koen.tca.android.state.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.android.state.AndroidEvents;
import com.koen.tca.android.wrapper.IMessage;
import com.koen.tca.android.wrapper.MessageChangeState;
import com.koen.tca.android.wrapper.RemoteMessageTransmitter;

import android.os.Handler;
import android.os.Message;

public class ThreadTest implements IThreadState {

	private Thread threadTest;
	private final String threadName = "Test";
	private Socket clientSocket;
	private Handler mainHandler;
	
	private ServerSocket androidServerSocket;
	private int timeout = 1000*5;
	
	private ThreadStartAction threadStartAction;
	
	// if this boolean is set to true, then the thread is stopping
	private boolean stopThread;
	
	public ThreadTest () {
		threadTest = null;
		clientSocket = null;
		mainHandler = null;
		setStopThread (true);
		threadStartAction = null;
	}
	
	public synchronized void startThread (Handler mainActivityHandler) {
		if (threadTest == null) {
			mainHandler = mainActivityHandler;
			threadTest = new Thread (this, threadName);
			threadTest.start();
		}
	}

	@Override
	public void run() {

		String messageType;
		IMessage remoteMsg = null;
		AndroidEvents event;
		
		// Gets the singleton object that holds the action to be run.
		ActionRunner actionRunner = ActionRunner.SINGLETON();
		
		// Gets the singleton object that holds the server IP address and port number
		DeviceIdentifier device = DeviceIdentifier.SINGLETON();
		
		// Create an object which can handle messages (sending/receiving) to and from the Server.
		RemoteMessageTransmitter messageTransmitter = new RemoteMessageTransmitter ();
		

		// check if there is an action object stored in the actionRunner
		if (actionRunner.getAction() != null) {
			
			// Start the Action in a separate thread.
			threadStartAction = new ThreadStartAction ();
			threadStartAction.startThread(mainHandler);
		}
		
		if (device.getServerIpAddress()!= null && device.getSocketPortNumber() != 0) {

			try {
				androidServerSocket = new ServerSocket (device.getSocketPortNumber());	

				// Set the timeout how long the server socket accept () method must hold.
				androidServerSocket.setSoTimeout(timeout);

				while (stopThread != true) {

					// waits for a command from the Server or a timeout occurs.
					Socket clientSocket = androidServerSocket.accept();
			
					remoteMsg = messageTransmitter.receiveMessage(clientSocket.getInputStream());
					if (remoteMsg != null) {
				
						// Gets the type of the message in a string value
						messageType = remoteMsg.toString();
				
						if (messageType.equals("ChangeState_Message")) {

							// gets the event from the received message
							event = ((MessageChangeState)remoteMsg).getEvent();
		 				
							// sends the event to the main thread (UI thread).
							Message msg = mainHandler.obtainMessage();
							msg.obj = event;
							mainHandler.sendMessage (msg);
					
							// go out the thread
							// TODO: stop the ThreadStartAction thread somehow.
							setStopThread (true);
						}
				
					}	
			
				} // while
				
			} catch (IOException e) {
				e.printStackTrace();
				// go out this run() method.
				return;
			} finally {
				if (androidServerSocket != null) {
					try {
						// Close the socket.
						androidServerSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}	// if
		
	}

	@Override
	public void stopThread() {
		threadStartAction = null;
		
	}
	
	private synchronized void setStopThread (boolean stopThread) {
		this.stopThread = stopThread;
	}
}


