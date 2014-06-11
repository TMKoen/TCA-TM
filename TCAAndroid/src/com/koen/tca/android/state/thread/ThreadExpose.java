package com.koen.tca.android.state.thread;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.android.state.AndroidEvents;
import com.koen.tca.android.wrapper.ExposeDataWrapper;

public class ThreadExpose implements Runnable {

	private Thread threadExpose;
	private final String threadName = "Expose";
	private Socket clientSocket;
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
	public void run() {

		// ExposeDevice has the IMEI and telephone number of the Android device and
		// it has also the Server IP address and the port number.
		ExposeDataWrapper dataWrapper = new ExposeDataWrapper ();

		// Gets the object thats has the IMEI and Telephone number of the phone.
		DeviceIdentifier device = DeviceIdentifier.SINGLETON();

		if (device.getServerIpAddress()!= null && device.getSocketPortNumber() != 0) {
	//		try {
	//			clientSocket = new Socket (device.getServerIpAddress(), device.getSocketPortNumber());
				
				// Sends the IMEI and telephone number to the Server.
//				dataWrapper.SendDeviceInfo(clientSocket.getOutputStream(), 
//						device.getImeiNumber(), 
//						device.getTelephoneNumber());
//				AndroidEvents event = dataWrapper.receiveEvent(clientSocket.getInputStream());
/*
			try {
				Thread.sleep(1000*30);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	*/
         

					
				AndroidEvents event = AndroidEvents.STOP_EXPOSE_NO_SERVER;
				// sends the event to the main thread (UI thread).
				Message msg = mainHandler.obtainMessage();
				msg.obj = event;
				mainHandler.sendMessage (msg);
				
//			} catch (UnknownHostException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
				if (clientSocket != null) {
					try {
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	//		}
		}
			
	

	}
	
}
