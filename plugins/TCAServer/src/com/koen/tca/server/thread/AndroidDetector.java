package com.koen.tca.server.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import com.koen.tca.common.message.AndroidEvents;
import com.koen.tca.common.message.ChangeStateMessage;
import com.koen.tca.common.message.IMessage;
import com.koen.tca.common.message.MessageExpose;
import com.koen.tca.common.message.RemoteMessageTransmitter;
//import com.koen.tca.server.message.AndroidEvents;
//import com.koen.tca.server.message.ActionMessage;
//import com.koen.tca.server.message.IMessage;
//import com.koen.tca.server.message.ChangeStateMessage;
//import com.koen.tca.server.message.MessageExpose;
import com.koen.tca.server.ICallBackClient;
import com.koen.tca.server.UEInfo;
//import com.koen.tca.server.message.RemoteMessageTransmitter;
import com.koen.tca.server.state.DetectResult;
	

/**
 * Detect Android devices.
 * @version
 * @author Koen Nijmeijer
 *
 */
public class AndroidDetector extends RemoteUserEquipment {

	// Handler to the server socket
	private ServerSocket serverSocket;
	
	// timeout is set to 5 seconds
	private final int timeout = 10000 * 5; 

	// Handler to the Thread and the name of the thread.
	private Thread androidThread;
	private final String threadName = "Android Thread";
	
	 // if this bit is true, than the thread must be stopped.
	private boolean stopThread;
	
	// Singleton object that stores the Android device information (IMEI and telephone number).
	private DetectResult detectResult;
	
	
	private ICallBackClient callBack;

	public AndroidDetector(int port) throws IOException {
		
		 // Handler to the new Thread
		androidThread = null;
		
		// the handler to the server socket
		serverSocket = null; 
		
		// Sets the port Number. Is the number not valid, port Number is 0 (random).
		setPortNumber(port); 
								
		// if this bit is true, than the running androidThread is stopping.
		stopThread = false; 
		
		// gets the singleton object that stored all the Android devices (IMEI and telephone number)
		detectResult = DetectResult.SINGLETON();
		
	}

	/**
	 * a new Thread is activated form the ActivateState () method in the class
	 * ServerStateDetect (implemented in class TestServer). This is the run()
	 * method of that Thread. It handles the actions for detecting Android
	 * devices (UE's).
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public void run() {

		// Holds the String name of the message that is send by the Android device.
		String messageType = null;
		
		// The message that was received from the Android device.
		IMessage remoteMsg = null;
		
		RemoteMessageTransmitter remoteMessageTransmitter = new RemoteMessageTransmitter();
		

		UEInfo clientUeInfo = null;
		String clientImei, clientName, clientNumber;
		
		
		try {
			serverSocket = new ServerSocket(getPortNumber());

			// sets the timeout how long the serverSocket.accepts () waits for a client
			serverSocket.setSoTimeout(timeout); 
		} catch (IOException e) {
			e.printStackTrace();

			// leaves this run() method.
			return;
		}

		// checks if stopThread () is not been called.
		while (stopThread != true) { 

			try {
				
				Socket clientSocket = serverSocket.accept();

				// gets the message from the Android clients
				remoteMsg = remoteMessageTransmitter.receiveMessage(clientSocket.getInputStream());

				if (remoteMsg != null) {
					messageType = remoteMsg.toString();

					// check if it is a Expose message
					if (messageType.equals("Expose_Message")) {

						// clientName ???? not needed i think?
						clientName = "toestel A";
						clientImei = ((MessageExpose) remoteMsg).getImei();
						clientNumber = ((MessageExpose) remoteMsg).getNumber();

						// creates a new UEInfo object from the Android client data
						// inputstream
						clientUeInfo = new UEInfo(clientImei, clientName,
								clientNumber);
						clientUeInfo.setIPAddress(clientSocket.getInetAddress()
								.toString());

						// Gets the singleton object with holds al the known Android device 
						List<UEInfo> ueList = detectResult.getValidUEList();

						int i = 0;
						for (; i != ueList.size()
								&& !ueList.get(i).getImei().equals(clientImei); i++) {
							// search for a UEInfo object with the same IMEI number
							// as the Android client.
						}

						if (i == ueList.size()) {
							// no UEInfo object in the list. Add a new one
							ueList.add(clientUeInfo);
						} else {
							// An object in the list has the same IMEI. Replace the
							// old object with clientUeInfo
							ueList.set(i, clientUeInfo);
						}

						// The Android device information is stored, 
						// so send a ChangeState message to the Android device.
						remoteMessageTransmitter.sendMessage(new ChangeStateMessage(AndroidEvents.STOP_EXPOSE),clientSocket.getOutputStream());
					}
				}

			} catch (SocketTimeoutException s) {
				// Exception is thrown if serverScoket.accept waits longer then
				// the timeout set by (setSoTimeout())
				// Necessary to check if stopThread is set to true
				break;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeConnection();
			}
		} // while
		
//		callBack.doyourthing here
		
		
		
	}

	/**
	 * This method starts a new thread where the server is listening to new
	 * Android devices (UE's)
	 */
	public synchronized void startThread() {
		// if there is another thread running, don't create a new thread
		if (androidThread == null) {
			androidThread = new Thread(this, threadName);
			androidThread.start();
		}
	}

	/**
	 * When the thread is running via the run() method, another thread can stop
	 * this thread via this method. thread.stop() is an abrupt method to stop a
	 * thread, so we use a boolean to do so.
	 */
	public synchronized void stopThread() {
		stopThread = true;
		do {
			// wait until the thread is totally stopped.
		} while (androidThread.isAlive());

		// safe? after stopThread = true, the thread is not directly stopped!!
		androidThread = null;
	}

	public synchronized void closeConnection() {
		try {
			if (!serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void setDetectResult(DetectResult detectResult) {
		this.detectResult = detectResult;
	}

	public void setCallBack(ICallBackClient callBack) {
		this.callBack = callBack;
		
	}
}
