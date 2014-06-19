package com.koen.tca.android.state.thread;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.android.state.AndroidEvents;
import com.koen.tca.android.wrapper.RemoteMessageTransmitter;
import com.koen.tca.android.wrapper.IMessage;
import com.koen.tca.android.wrapper.MessageChangeState;
import com.koen.tca.android.wrapper.MessageExpose;

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
	private Socket clientSocket;
	private Handler mainHandler;
	private final int socketTimeout = 30000;
	
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
		AndroidEvents event = AndroidEvents.STOP_EXPOSE_NO_SERVER;
		
		// Create an object which can handle messages (sending/receiving) to and from the Server.
		RemoteMessageTransmitter messageTransmitter = new RemoteMessageTransmitter ();

		// Gets the object thats has the IMEI and Telephone number of the phone.
		DeviceIdentifier device = DeviceIdentifier.SINGLETON();

		if (device.getServerIpAddress()!= null && device.getSocketPortNumber() != 0) {
			try {
				InetSocketAddress serverAddress = new InetSocketAddress (device.getServerIpAddress(), device.getSocketPortNumber());
				
				clientSocket = new Socket();
				clientSocket.connect(serverAddress,socketTimeout);
		
// 				old method without timeout!!!				
//				clientSocket = new Socket (device.getServerIpAddress(), device.getSocketPortNumber());
	
				// Sends a MessageExpose message to the Server.
				// MessageExpose has the IMEI and telephone number to the Android device.
				messageTransmitter.sendMessage(new MessageExpose(device.getImeiNumber(), 
						device.getTelephoneNumber()), 
						clientSocket.getOutputStream());

				changeStateMsg = messageTransmitter.receiveMessage(clientSocket.getInputStream());

				// The server must send a ChangeState message after he receives Expose message. 
				if (changeStateMsg instanceof MessageChangeState ) {
					// gets the event from the received message
					event = ((MessageChangeState)changeStateMsg).getEvent();
				} else {
					// something goes wrong. te server must send a ChangeState messages after exposer
				}
	 				
				// sends the event to the main thread (UI thread).
				Message msg = mainHandler.obtainMessage();
				msg.obj = event;
				mainHandler.sendMessage (msg);
				
			} catch (UnknownHostException e) {
				// Timeout was occurred 

				
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// No valid host address. Thrown by clientsocket.connect(..)
			} catch (ConnectException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				// Timeout. No server found!
				Message msg = mainHandler.obtainMessage();
				msg.obj = AndroidEvents.STOP_EXPOSE_NO_SERVER;
				mainHandler.sendMessage(msg);
				
			} finally {
				if (clientSocket != null) {
					try {
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
			
	

	}

	
}
