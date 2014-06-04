package com.koen.tca.android.state;

import android.os.Handler;
import android.os.Looper;

import com.koen.tca.android.state.thread.ThreadExpose;

public class AndroidStateExpose implements IAndroidState {

	private ThreadExpose exposeThread;
	
	public AndroidStateExpose () {
		Handler mainHandler = new Handler (Looper.getMainLooper());
		exposeThread = new ThreadExpose (mainHandler);
	}
	
	@Override
	public void changeState(AndroidEvents androidEvent,
			AndroidStateMachine androidStateMachine) {
	
		switch (androidEvent) {
		case STOP_EXPOSE:
			androidStateMachine.setState(new AndroidStateReady());
			break;
		case STOP_EXPOSE_NO_SERVER:
			androidStateMachine.setState(new AndroidStateIdle());
			break; 
		default:
			break;
		}

	}

	@Override
	public void activateState() {
		// Start a new Thread that runs the state: Expose
		exposeThread.startThread();

		
		// Returns to the client while the new thread is running.
	}

	/**
	 * Override the default toString method to return "Expose"
	 */
	@Override
	public String toString () {
		
		return "Android Expose";
		
	}
}
