package com.koen.tca.android.state;

import android.os.Handler;

import com.koen.tca.android.state.thread.ThreadReady;
import com.koen.tca.common.message.AndroidEvents;


/**
 * Represents the Ready state
 * @version 1.0
 * @author Koen
 *
 */
public class AndroidStateReady extends AbstractAndroidState {

	// The ready thread
	private ThreadReady threadReady;
	
	// Constructor
	public AndroidStateReady (ThreadReady threadReady) {
		this.threadReady = threadReady;
	}
	
	/**
	 * Change the state of the Android. It can be Idle, Test or Expose.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param androidEvent the AndroidEvents event where to change to.
	 * @param androidStateMachine the AndroidStateMachine to call back.
	 * @return the new IAndroidState object.
	 */
	@Override
	public IAndroidState changeState(AndroidEvents androidEvent,
			AndroidStateMachine androidStateMachine) {
		super.changeState(androidEvent, androidStateMachine);
		
		IAndroidState state = this;
		switch (androidEvent) {
		case IDLE:
			state = getStateMachine().createState(AndroidStates.IDLE);
			break;
		case START_TEST:
			state = getStateMachine().createState(AndroidStates.TEST);
			break;
		case START_EXPOSE:
			state = getStateMachine().createState(AndroidStates.EXPOSE);
		default:
			break;
		}

		return state;
	}

	/**
	 * Activate the state.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param mainActivityHandler the handler to send messages back to the UI thread.
	 */
	@Override
	public void activateState(Handler mainActivityHandler) {
		super.activateState(mainActivityHandler);
		
		// Start a new Thread that runs the state: Ready
		threadReady.startThread(mainActivityHandler);
		// Now returns to the main thread while the new thread is running.
	}

	/**
	 * Override the default toString method to return "Ready"
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @return the String message.
	 */
	@Override
	public String toString () {
		
		return "Android Ready state";
		
	}
}
