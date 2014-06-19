package com.koen.tca.android.state.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.koen.tca.android.ActionObjects;
import com.koen.tca.android.ActionRunner;
import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.android.action.ITestAction;
import com.koen.tca.android.state.AndroidEvents;
import com.koen.tca.android.wrapper.IMessage;
import com.koen.tca.android.wrapper.IntentAction;
import com.koen.tca.android.wrapper.MessageChangeState;
import com.koen.tca.android.wrapper.ReadyDataWrapper;
import com.koen.tca.android.wrapper.RemoteMessageTransmitter;
import com.koen.tca.android.wrapper.MessageAction;

import android.os.Handler;
import android.os.Message;

public class ThreadReady implements IThreadState {

	private Thread readyThread;
	private Handler mainHandler;
	private boolean stopThread;
	private ServerSocket androidServerSocket;
	private int timeout = 1000*5;
	
	final String threadName = "Ready thread";
	public ThreadReady () {
		readyThread = null;
		mainHandler = null;
		setStopThread(true);
	}
	
	
	public synchronized void startThread (Handler mainActivityHandler) {
		if (readyThread == null) {
			mainHandler = mainActivityHandler;
			readyThread = new Thread (this, threadName);
			setStopThread(false);
			readyThread.start();
		}
	}
	
	public synchronized void stopThread () {
		// Stops the thread
		setStopThread (true);
		readyThread = null;
		mainHandler = null;
	}
	
	@Override
	public void run() {
		
		String messageType;
		IMessage remoteMsg = null;
		AndroidEvents event;
		
		// Create an object which can handle messages (sending/receiving) to and from the Server.
		RemoteMessageTransmitter messageTransmitter = new RemoteMessageTransmitter ();
				
		DeviceIdentifier device = DeviceIdentifier.SINGLETON();
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
						messageType = remoteMsg.toString();
						
						switch (messageType) {
						case "Action_Message":

							IntentAction intentAction = ((MessageAction) remoteMsg).getAction();

							try {
								// checks if the action name is a valid member of the ActionObjects enum.
								// the value of this enum name is not important. 
								// If its not a member of this enum, throws an IllegalArgumentException.
								ActionObjects.valueOf(intentAction.getName());

								ActionRunner actionRunner = ActionRunner.SINGLETON();
								
								
								
								// creates dynamically a ITestAction object depends on the name of the action.
								// The classname must be a fully qualified name, so inclusive the package name.
								try {
									actionRunner.setAction((ITestAction) Class.forName("com.koen.tca.android.action."+intentAction.getName()).newInstance());
								} catch (InstantiationException
										| IllegalAccessException
										| ClassNotFoundException e) {
									// should not by happening because the dynamically created class is of type: ITestAction 
									// e.printStackTrace();
								}
								
							} catch (IllegalArgumentException e) {
								// the name is not a member of the IntenAction enum!!
								e.printStackTrace();
							}
							
							break;
						
						case "ChangeState_Message":
			
							// gets the event from the received message
							event = ((MessageChangeState)remoteMsg).getEvent();
				 				
							// sends the event to the main thread (UI thread).
							Message msg = mainHandler.obtainMessage();
							msg.obj = event;
							mainHandler.sendMessage (msg);
							
							// go out the thread
							setStopThread (true);
							break;
						case "Expose_Message":
							// must not be received in the Ready state, so do nothing.
							break;
						case "Command_Message":
							// TODO: get results, etc.
						default:
							// must not be happening!
							break;
							
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
		} //iff

	}	// method run ()
	
	private synchronized void setStopThread (boolean stopThread) {
		this.stopThread = stopThread;
	}
}	// class ThreadReady
		
