package com.koen.tca.android.state;

import com.koen.tca.android.state.thread.ThreadTest;
import com.koen.tca.common.message.AndroidEvents;

import android.os.Handler;


public class AndroidStateTest implements IAndroidState {

	private ThreadTest threadTest;
	
	public AndroidStateTest () {
		threadTest = new ThreadTest ();
	}
	
	@Override
	public void changeState(AndroidEvents androidEvent,
			AndroidStateMachine androidStateMachine) {
		
		switch (androidEvent) {
		case FINISHED:
			androidStateMachine.setState(new AndroidStateReady());
			break;
		case STOP_TEST:
			androidStateMachine.setState(new AndroidStateIdle());
			break;
		default:
			break;
		}

	}

	@Override
	public void activateState(Handler mainActivityHandler) {
		// Start a new Thread that runs the state: Ready
		threadTest.startThread(mainActivityHandler);
		// Now returns to the main thread while the new thread is running.

	}

	/**
	 * Override the default toString method to return "Test"
	 */
	@Override
	public String toString () {
		
		return "Android Test";
		
	}
}
