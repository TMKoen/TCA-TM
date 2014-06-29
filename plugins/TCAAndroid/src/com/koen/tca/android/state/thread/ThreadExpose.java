package com.koen.tca.android.state.thread;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;

import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.common.message.AcknowledgeMessage;
import com.koen.tca.common.message.AndroidEvents;
import com.koen.tca.common.message.ChangeStateMessage;
import com.koen.tca.common.message.ExposeMessage;
import com.koen.tca.common.message.IMessage;
import com.koen.tca.common.message.RemoteMessageTransmitter;

/**
 * Runs the Thread for the Expose state.
 * <p>
 * The thread is sending a Expose message to the Server so that the Server knows this device.
 * After that, the Server sends a Change State message back and this thread is stopping.
 * the android device goes to the Ready state or the Idle state is something is wrong.
 * @version
 * @author Koen Nijmeijer
 *
 */
public class ThreadExpose implements IThreadState {

	private Thread threadExpose;
	private final String threadName = "Expose";

	// The client socket which connect to the server.
	private Socket clientSocket;
	
	// timeout for reading (received) messages 
	private final int readTimeout = 1000*30;
	// timeout for the socket to wait for a connection
	private final int socketTimeout = 1000*60;	

	
	private Handler mainHandler;

	
	public ThreadExpose () {
		threadExpose = null;
		clientSocket = null;
		mainHandler = null;

	}
	
	public synchronized void startThread (Handler mainActivityHandler) {
		if (threadExpose == null) {
			mainHandler = mainActivityHandler;
			threadExpose = new Thread (this, threadName);
			threadExpose.start();
		}
	}
	
	@Override
	public void stopThread() {
		// Because this thread stops direct after the exposer, this method is not used. 
		
	}	

	@Override
	public void run() {

		IMessage changeStateMsg = null;
		
		// Initialize the default AndroidEvent if there is no even received from the server.
		AndroidEvents event = AndroidEvents.STOP_EXPOSE_NO_SERVER;
		
		// Create an object which can handle messages (sending/receiving) to and from the Server.
		RemoteMessageTransmitter messageTransmitter = new RemoteMessageTransmitter ();

		// Gets the object thats has the IMEI and Telephone number of the phone.
		DeviceIdentifier device = DeviceIdentifier.SINGLETON();

		if (device.getServerIpAddress()!= null && device.getSocketPortNumber() != 0) {
			try {
				InetSocketAddress serverAddress = new InetSocketAddress (device.getServerIpAddress(), device.getSocketPortNumber());
				
				clientSocket = new Socket();
				
				// connect to the server with a timeout interval if the server is not responding.
				clientSocket.connect(serverAddress,socketTimeout);
		
// 				old method without timeout!!!				
//				clientSocket = new Socket (device.getServerIpAddress(), device.getSocketPortNumber());
	
				// Sends a MessageExpose message to the Server.
				// MessageExpose has the IMEI and telephone number to the Android device.
				messageTransmitter.setOutputStream(clientSocket.getOutputStream());

				// Send a Expose message to the server.
				messageTransmitter.sendMessage(new ExposeMessage (device.getImeiNumber(), 
						device.getTelephoneNumber()));

				// Initialize the input stream.
				messageTransmitter.setInputStream(clientSocket.getInputStream());

				// receive a ChangeState message from the server.
				changeStateMsg = messageTransmitter.receiveMessage(clientSocket, readTimeout );

				// The server must send a ChangeState message after he receives Expose message. 
				// If there was an IO exception or a timeout, than the message = null.
				if (changeStateMsg != null && changeStateMsg instanceof ChangeStateMessage ) {
					// gets the event from the received message
					event = ((ChangeStateMessage)changeStateMsg).getEvent();
					
					// Sends an Acknowledge message to the server with a text: "Close".
					messageTransmitter.sendMessage(new AcknowledgeMessage("Close"));
					
				} else {
					// something goes wrong. the server must send a ChangeState messages after expose.
					// An IO exception can occurred or a timeout while waiting on a message.
					// No Acknowledge has to be send because of the IO exception or the timeout.
					// the event = STOP_EXPOSE_NO_SERVER
				}
	 								
			} catch (UnknownHostException e) {
				// Timeout was occurred 

				
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// No valid host address. Thrown by clientsocket.connect(..)

			} catch (ConnectException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				messageTransmitter.closeOutputStream();
				messageTransmitter.closeInputStream();
				
				if (clientSocket != null) {
					try {
						clientSocket.close();			

					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// sends the event to the main thread (UI thread).
					Message msg = mainHandler.obtainMessage();
					msg.obj = event;
					mainHandler.sendMessage (msg);
				}
			}
		}
	} // run ()
}
