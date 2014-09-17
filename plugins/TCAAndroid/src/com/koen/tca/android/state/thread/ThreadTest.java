package com.koen.tca.android.state.thread;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.common.message.AndroidEvents;
import android.os.Handler;
import android.os.Message;

/**
 * The Thread for the test state. When the Test state is running, the Server can't communicate with the Android.
 * After the Test state is finished, the Android goes back to the Ready state where the server can communicate with.
 * @version 1.0
 * @author Koen Nijmeijer
 *
 */
public class ThreadTest implements IThreadState {

	// The thread handler and its name
	private Thread threadTest;
	private final String threadName = "Test";

	// Handler to the UI for the call back message.
	private Handler mainHandler;
	
	
	public ThreadTest () {
		threadTest = null;
		mainHandler = null;
	}
	
	/**
	 * Starts the thread.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param mainActiviyHandler the Handler to the main UI thread for call back
	 */
	@Override
	public void startThread (Handler mainActivityHandler) {
		if (threadTest == null) {
			mainHandler = mainActivityHandler;
			threadTest = new Thread (this, threadName);
			threadTest.start();
		}
	}

	@Override
	public void stopThread() {
		// the thread stops automatically if the Action is finished.
	}
	
	@Override
	public void run() {

		AndroidEvents event;
		
		// Gets the singleton object that holds the action to be run.
		ActionRunner actionRunner = ActionRunner.SINGLETON();
		
		// check if there is an action object stored in the actionRunner
		if (actionRunner.getAction() != null) {
	
			// Start the test
			actionRunner.startTest();
		}

		// Event to stop the Test state and goes back to the Ready state.
		event = AndroidEvents.FINISHED;
	
		// sends the event to the main thread (UI thread).
		Message msg = mainHandler.obtainMessage();
		msg.obj = event;
		mainHandler.sendMessage (msg);
		
	}
	
}


