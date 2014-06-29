package com.koen.tca.android.state;

import android.os.Handler;

import com.koen.tca.android.state.thread.ThreadExpose;
import com.koen.tca.common.message.AndroidEvents;

/**
 * represents the Expose state.
 * <p>
 * This class implements the <code>IAndroidState</code> interface
 * @version
 * @author Koen Nijmeijer
 *
 */
public class AndroidStateExpose implements IAndroidState {

	private ThreadExpose threadExpose;
	
	public AndroidStateExpose () {

		threadExpose = new ThreadExpose ();
	}
	
	/**
	 * Change the present state (Expose state) to Ready state or Idle state.
	 * <p>
	 * if androidStateMachine can't make a connection to the Server, than the
	 * android goes back to the Idle state. Otherwise it goes to the Ready state.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 */
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

	/**
	 * Activate the state.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public void activateState(Handler mainActivityHandler) {
		// Start a new Thread that runs the state: Expose
		threadExpose.startThread(mainActivityHandler);

		// Now returns to the main thread while the new thread is running.
	}

	/**
	 * Override the default toString method to return "Android Expose"
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public String toString () {
		
		return "Android Expose";
		
	}
}
