package com.koen.tca.android.state;

import android.os.Handler;

import com.koen.tca.android.state.thread.ThreadReady;
import com.koen.tca.common.message.AndroidEvents;



public class AndroidStateReady implements IAndroidState {

	private ThreadReady threadReady;
	
	public AndroidStateReady () {
		threadReady = new ThreadReady ();
	}
	
	@Override
	public void changeState(AndroidEvents androidEvent,
			AndroidStateMachine androidStateMachine) {
		
		switch (androidEvent) {
		case IDLE:
			androidStateMachine.setState(new AndroidStateIdle());
			break;
		case START_TEST:
			androidStateMachine.setState(new AndroidStateTest());
			break;
		case START_EXPOSE:
			androidStateMachine.setState(new AndroidStateExpose());
		default:
			break;
		}

	}

	@Override
	public void activateState(Handler mainActivityHandler) {
		// Start a new Thread that runs the state: Ready
		threadReady.startThread(mainActivityHandler);
		// Now returns to the main thread while the new thread is running.
	}

	/**
	 * Override the default toString method to return "Ready"
	 */
	@Override
	public String toString () {
		
		return "Android Ready";
		
	}
}
