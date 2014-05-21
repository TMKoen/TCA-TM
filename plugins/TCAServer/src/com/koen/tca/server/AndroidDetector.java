package com.koen.tca.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import com.koen.tca.server.state.DetectResult;
	

/**
 * 
 * @author Christophe
 *
 */
public class AndroidDetector extends RemoteUserEquipment {

	private ServerSocket serverSocket;
	private final int timeout = 10000 * 5; // timeout is set to 5 seconds

	private Thread androidThread;
	private String threadName;
	private boolean stopThread; // if this bit is true, than the thread must be
								// stopped.

	private BufferedInputStream inputStream;
	private BufferedOutputStream outputStream;

	private DetectResult detectResult;
	private ICallBackClient callBack;

	public AndroidDetector(int port) throws IOException {
		androidThread = null; // Handler to the new Thread
		threadName = "Android Thread";
		serverSocket = null; // the handler to the server socket
		setPortNumber(port); // Sets the port Number. Is the number not valid,
								// port Number is 0 (random).
		stopThread = false; // if this bit is true, than the running
							// androidThread is stopping.
		inputStream = null; // inputstream form the android device
		outputStream = null; // outputstream to the android device
	}

	/**
	 * a new Thread is activated form the ActivateState () method in the class
	 * ServerStateDetect (implemented in class TestServer). This is the run()
	 * method of that Thread. It handles the actions for detecting Android
	 * devices (UE's).
	 */
	@Override
	public void run() {

		try {
			serverSocket = new ServerSocket(getPortNumber());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		while (stopThread != true) { // checks if stopThread () is not been
										// called.

			try {
				serverSocket.setSoTimeout(timeout); // sets the timeout how long
													// serverSocket.accepts ()
													// waits for a client
				Socket clientSocket = serverSocket.accept();

				inputStream = new BufferedInputStream(
						clientSocket.getInputStream());
				outputStream = new BufferedOutputStream(
						clientSocket.getOutputStream());

				// reader (BufferReader) reads a hole String form the
				// inputStream
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));

				UEInfo clientUeInfo = null;
				String clientImei, clientName, clientNumber;

				// blocks the thread until all the data is received from the
				// Android device.
				if ((clientImei = reader.readLine()) != null
						&& (clientName = reader.readLine()) != null
						&& (clientNumber = reader.readLine()) != null) {

					// creates a new UEInfo object from the Android client data
					// inputstream
					clientUeInfo = new UEInfo(clientImei, clientName,
							clientNumber);
					clientUeInfo.setIPAddress(clientSocket.getInetAddress()
							.toString());

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

				} else {
					// Not all the data (IMEI, Name and Telephone number) is
					// send/received from the android device.
				}

				// TODO: send acknowledge message to the UE
				// TODO: writer.writeln ("ACK ..");
				// TODO: flush()??

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
		}
		
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

	public void closeConnection() {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (!serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setDetectResult(DetectResult detectResult) {
		this.detectResult = detectResult;
	}

	public void setCallBack(ICallBackClient callBack) {
		this.callBack = callBack;
		
	}
}
