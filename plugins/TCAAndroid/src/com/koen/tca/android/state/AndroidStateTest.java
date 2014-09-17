package com.koen.tca.android.state;

import com.koen.tca.android.state.thread.ThreadTest;
import com.koen.tca.common.message.AndroidEvents;

import android.os.Handler;

/**
 * Represents the Test state
 * @version 1.0
 * @author Koen
 *
 */
public class AndroidStateTest extends AbstractAndroidState {

	// The new Thread
	private ThreadTest threadTest;
	
	// Constructor
	public AndroidStateTest (ThreadTest threadTest) {
		this.threadTest = threadTest;
	}

	/**
	 * Change the state of the Android. It can be Ready or Idle.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param androidEvent the AndroidEvents event thats change the state
	 * @param androidStateMachine the AndroidStateMachine to call back to.
	 * @return the new IAndroidState state.
	 */
	@Override
	public IAndroidState changeState(AndroidEvents androidEvent,
			AndroidStateMachine androidStateMachine) {
		super.changeState(androidEvent, androidStateMachine);
		
		IAndroidState state = this;
		switch (androidEvent) {
		case FINISHED:
			getStateMachine().createState(AndroidStates.READY);
			break;
		case STOP_TEST:
			getStateMachine().createState(AndroidStates.IDLE);
			break;
		default:
			break;
		}

		return state;
	}

	/**
	 * Activates the state.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param mainActivityHandler the Handler to send messages back to the UI thread.
	 */
	@Override
	public void activateState(Handler mainActivityHandler) {
		super.activateState(mainActivityHandler);
		
		// Start a new Thread that runs the state: Ready
		threadTest.startThread(mainActivityHandler);
		// Now returns to the main thread while the new thread is running.

	}

	/**
	 * Override the default toString method to return "Test"
	 * @return the String message.
	 */
	@Override
	public String toString () {
		
		return "Android Test state";
		
	}
}
