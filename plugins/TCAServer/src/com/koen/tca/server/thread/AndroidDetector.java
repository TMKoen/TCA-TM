package com.koen.tca.server.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import com.koen.tca.common.message.AcknowledgeMessage;
import com.koen.tca.common.message.AndroidEvents;
import com.koen.tca.common.message.ChangeStateMessage;
import com.koen.tca.common.message.ExposeMessage;
import com.koen.tca.common.message.IMessage;
import com.koen.tca.common.message.RemoteMessageTransmitter;
import com.koen.tca.server.ICallBackClient;
import com.koen.tca.server.UEInfo;
import com.koen.tca.server.state.DetectResult;
import com.koen.tca.server.state.IStateCallBack;

/**
 * Detect Android devices.
 * 
 * @version
 * @author Koen Nijmeijer
 * 
 */
public class AndroidDetector extends RemoteUserEquipment {

	// Handler to the server socket
	private ServerSocket serverSocket;

	// Timeout is set to 60 seconds
	private final int timeout = 1000 * 15;

	// Timeout for reading messages from an input stream.
	private final int readTimeout = 1000 * 15;

	// Handler to the Thread and the name of the thread.
	private Thread androidThread;

	// The name of our Thread.
	private final String threadName = "Android Thread";

	// If this bit is true, than the thread must be stopped.
	private boolean stopThread;

	// Singleton object that stores the Android device information (IMEI and
	// telephone number).
	private DetectResult detectResult;

	// A callback.
	// When we timeout, we fire call back failed.
	@SuppressWarnings("unused")
	private ICallBackClient callBack;

	private IStateCallBack stateCallBack;

	public AndroidDetector(int port) throws IOException {

		// Handler to the new Thread
		androidThread = null;

		// the handler to the server socket
		serverSocket = null;

		// Sets the port Number. Is the number not valid, port Number is 0
		// (random).
		setPortNumber(port);

		// if this bit is true, than the running androidThread is stopping.
		stopThread = false;

		// gets the singleton object that stored all the Android devices (IMEI
		// and telephone number)
		detectResult = DetectResult.SINGLETON();

	}

	/**
	 * a new Thread is activated from the <code>ActivateState ()</code> method
	 * in the class <code>ServerStateDetect</code> (implemented in class
	 * TestServer). This is the run() method of that Thread. It handles the
	 * actions for detecting Android devices (UE's).
	 * <p>
	 * This thread stays in a loop until the user calls
	 * <code>stopThread ()</code>. In the loop, we are waiting for a connection
	 * establishing with a Android device. After the connection is made, the
	 * server receives a <code>ExposeMessage</code> message with holds the IMEI
	 * and the telephone number of the Android device. After this, the server
	 * sends a <code>ChangeStateMessage</code> message so Android device can go
	 * to the Ready state. The Android device sends a
	 * <code>AcknowledgeMessage</code> message to the Server and close the
	 * connection. PS: The Android device must first close the connection before
	 * the server does. Otherwise the server goes in an WAIT_TIME state.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public void run() {

		// The socket for the connection with the Android device.
		Socket clientSocket = null;

		// The message that was received from the Android device.
		IMessage remoteMsg = null;

		RemoteMessageTransmitter remoteMessageTransmitter = new RemoteMessageTransmitter();

		try {
			serverSocket = new ServerSocket(getPortNumber());

			// checks if stopThread () is not been called.
			while (stopThread != true) {

				try {

					// sets the timeout how long the serverSocket.accepts ()
					// waits for a client.
					serverSocket.setSoTimeout(timeout);

					// waits for an incoming connection request from an Android
					// device.
					clientSocket = serverSocket.accept();

					System.out.println("Server has found a telephone!");

					// Initialize a ObjectInputStread. It blocks until
					// ObjectOutputStream on the Android device flush it.
					remoteMessageTransmitter.setInputStream(clientSocket
							.getInputStream());

					// Fetch the message form the Android device or null if
					// there was no message after a timeout or an exception was
					// thrown.
					remoteMsg = remoteMessageTransmitter.receiveMessage(
							clientSocket, readTimeout);

					System.out
							.println("Server has received a message from the Android");

					// check if the message was send. If there was an exception
					// or timeout, then remoteMsg must be null.
					if (remoteMsg != null && remoteMsg instanceof ExposeMessage) {

						// check if it is a Expose message

						System.out
								.println("Server has received a Expose message");

						// Store the Android device info in the list.
						storeDeviceInfo(((ExposeMessage) remoteMsg).getImei(),
								((ExposeMessage) remoteMsg).getNumber(),
								clientSocket.getInetAddress().toString());

						// Initialize the ObjectOutputStream.
						remoteMessageTransmitter.setOutputStream(clientSocket
								.getOutputStream());

						// Send the ChangeState (STOP_EXPOSE) message to the
						// android device.
						remoteMessageTransmitter
								.sendMessage(new ChangeStateMessage(
										AndroidEvents.STOP_EXPOSE));

						System.out
								.println("Server has send a ChangeState message");

						// receive an Acknowledge message from the Android
						// device, so that the server
						// knows that the android device closed the connection
						// first!!
						remoteMsg = remoteMessageTransmitter.receiveMessage(
								clientSocket, readTimeout);

						if (remoteMsg != null
								&& remoteMsg instanceof AcknowledgeMessage) {
							System.out
									.println("Server has received a Acknowledge message from the Android");
							// close the Inputstream, OutputStream and the
							// clientSocket.
							remoteMessageTransmitter.closeInputStream();
							remoteMessageTransmitter.closeOutputStream();
							if (!clientSocket.isClosed()) {
								clientSocket.close();
							}
						} else {
							System.out
									.println("Error: NO acknowledge message received.");
							// no valid Acknowledge message received.
							remoteMessageTransmitter.closeInputStream();
							remoteMessageTransmitter.closeOutputStream();

							// because there was no Acknowledge message from the
							// Android device, it is not sure that
							// the Android first stopped the connection. So it
							// is possible that the server comes in a
							// TIME_WAIT state what can takes seconds/minutes
							// before the server can go on.
							if (!clientSocket.isClosed()) {
								clientSocket.close();
							}

						}

					} else {
						// Wrong message send by the Android device.It must be
						// an ExposeMessage.
						System.out
								.println("Error: Android device must send a Expose message");

						/**
						 * because the Android device don't send am Expose
						 * message, it is not clear in which state the Android
						 * is. So we can't send any other messages to them. Just
						 * close the connection with that device and go on.
						 **/

						// Close the inputstream
						remoteMessageTransmitter.closeInputStream();

						// Close the client socket.
						if (!clientSocket.isClosed()) {
							clientSocket.close();
						}
						System.out
								.println("Connection to the Android device is closed (no referenc is stored).");
					}

				} catch (SocketException e) {
					// Error in the underlying protocol (TCP for example)
					// Throws by setSoTimeOut.

					// try to close the InputStream, OutputStream and the
					// clientSocket.
					remoteMessageTransmitter.closeInputStream();
					remoteMessageTransmitter.closeOutputStream();
					if (!clientSocket.isClosed()) {
						clientSocket.close();
					}
					e.printStackTrace();

				} catch (SocketTimeoutException e) {
					// A timeout for the blocking method: accept() occurred.
					// Throws by setSoTimeOut.

				} catch (IOException e) {
					// IO error occurred while waiting for an socket connection.
					// IO error occurred when closing the clientSocket.
					// Throws by ServerSocket.accept() and clientsocket.close().
					e.printStackTrace();
				}

			} // while

		} catch (IOException e) {
			// IO error while opening the Server socket.
			// Throws by new ServerSocket (..).

			e.printStackTrace();

		} catch (IllegalArgumentException e) {
			// The port number is out of range (0 - 65535)
			// Throws by new ServerSocket (..).
			e.printStackTrace();
		} finally {
			closeServerConnection();
			stateCallBack.ending();
		}

	} // run ()

	/**
	 * Stores the android device information in the list.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @see UEInfo
	 * @see DetectResult
	 * @param imei
	 * @param number
	 * @param ipAddress
	 */
	public synchronized void storeDeviceInfo(String imei, String number,
			String ipAddress) {

		// Creates a new UEInfo object from the Android device data.
		UEInfo clientUeInfo = new UEInfo(imei, "", number);
		clientUeInfo.setIPAddress(ipAddress);

		// Gets the singleton object with holds all the known Android devices.
		List<UEInfo> ueList = detectResult.getValidUEList();

		int i = 0;
		for (; i != ueList.size() && !ueList.get(i).getImei().equals(imei); i++) {
			// search for a UEInfo object with the same IMEI number as the
			// Android client.
		}

		if (i == ueList.size()) {
			// No UEInfo object in the list so add a new one.
			ueList.add(clientUeInfo);
		} else {
			// An object in the list has the same IMEI so replace the one.
			ueList.set(i, clientUeInfo);
		}
	}

	/**
	 * This method starts a new thread where the server is listening to new
	 * Android devices (UE's)
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 */
	public synchronized void startThread(IStateCallBack stateCallBack) {

		this.stateCallBack = stateCallBack;

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

		System.out.println("Stopping detecting mode....");

		// TODO: This must work somehow! Know it blocks..
		/*
		 * try { androidThread.join(); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */

		do {
			try {

				this.wait(1000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// wait until the thread is totally stopped.
		} while (androidThread.isAlive());

		// safe? after stopThread = true, the thread is not directly stopped!!
		androidThread = null;
	}

	/**
	 * Close the Server connection if it is still open.
	 * <p>
	 * Only accessible by this class only (private).
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 */
	private synchronized void closeServerConnection() {
		try {
			if (!serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param detectResult
	 */
	public synchronized void setDetectResult(DetectResult detectResult) {
		this.detectResult = detectResult;
	}

	/**
	 * Sets the callback method that is called when the tread is going to end.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param callBack
	 *            the method to execute when the thread is going to stop.
	 */
	public void setCallBack(ICallBackClient callBack) {
		this.callBack = callBack;
	}

	/**
	 * Overrides the toString method.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public String toString() {
		return "I am detecting android UE's on port:" + this.getPortNumber()
				+ "\ndetection result (sofar) is: " + detectResult.toString();

	}

}
