package com.koen.tca.android.state.thread;

import java.net.Socket;

import android.os.Handler;

public class ThreadTest implements Runnable {

	private Thread threadTest;
	private final String threadName = "Test";
	private Socket clientSocket;
	private Handler mainHandler;
	
	public ThreadTest () {
		threadTest = null;
		clientSocket = null;
		mainHandler = null;
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
		// TODO Auto-generated method stub
		
	}
}


