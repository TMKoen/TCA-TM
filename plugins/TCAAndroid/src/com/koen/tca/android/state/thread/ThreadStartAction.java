package com.koen.tca.android.state.thread;

import com.koen.tca.android.ActionRunner;

import android.os.Handler;

public class ThreadStartAction implements IThreadState {

	// Handler to this thread
	private Thread startActionThread;
	private Handler mainHandler;


	// The name of this thread
	final String threadName = "Ready thread";
	
	// Sets the handler to the singleton ActionRunner.
	private ActionRunner actionRunner = ActionRunner.SINGLETON();
	
	public ThreadStartAction () {
		startActionThread = null;
		mainHandler = null;
	}
	@Override
	public void run() {
		
		// Starts the Action that is stored in the singleton actionRunner.
		actionRunner.startTest();
		// TODO: send event.Finished ?
		
		// End of the thread.
	}

	@Override
	public synchronized void startThread(Handler mainActivityHandler) {
		if (startActionThread == null) {
			mainHandler = mainActivityHandler;
			startActionThread = new Thread (this, threadName);
			startActionThread.start();
		}
		
	}

	@Override
	public synchronized void stopThread() {
		// Stops the thread

		// TODO: how stop the thread safely?
		startActionThread = null;
		mainHandler = null;
		
	}
}
