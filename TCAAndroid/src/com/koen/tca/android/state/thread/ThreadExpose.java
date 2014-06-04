package com.koen.tca.android.state.thread;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.android.ExposeDevice;
import com.koen.tca.android.state.AndroidEvents;

public class ThreadExpose implements Runnable {

	private Thread exposeThread;
	private final String androidThreadName = "Expose";
	private Socket clientSocket;
	private Handler mainHandler;
	
	
	public ThreadExpose (Handler mainHandler) {
		exposeThread = null;
		clientSocket = null;
		this.mainHandler = mainHandler;

	}
	
	public synchronized void startThread () {
		if (exposeThread == null) {
			exposeThread = new Thread (this, androidThreadName);
			exposeThread.start();
		}
	}
	

	@Override
	public void run() {

		// ExposeDevice has the IMEI and telephone number of the Android device and
		// it has also the Server IP address and the port number.
		ExposeDevice exposeDevice = new ExposeDevice ();

		// Gets the object thats has the IMEI and Telephone number of the phone.
		DeviceIdentifier device = DeviceIdentifier.SINGLETON();

		if (device.getServerIpAddress()!= null && device.getSocketPortNumber() != 0) {
			try {
				clientSocket = new Socket (device.getServerIpAddress(), device.getSocketPortNumber());
				
				// Sends the IMEI and telephone number to the Server.
				exposeDevice.SendDeviceInfo(clientSocket.getOutputStream(), 
						device.getImeiNumber(), 
						device.getTelephoneNumber());
				AndroidEvents event = exposeDevice.ReceiveEvent(clientSocket.getInputStream());

				// sends the event to the main thread (UI thread).
				Message msg = Message.obtain();
				msg.obj = event;
				mainHandler.sendMessage (msg);
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
