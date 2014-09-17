package com.koen.tca.android.state;

import android.os.Handler;

import com.koen.tca.android.state.thread.ThreadExpose;
import com.koen.tca.common.message.AndroidEvents;

/**
 * represents the Expose state.
 * <p>
 * @version
 * @author Koen Nijmeijer
 *
 */
public class AndroidStateExpose extends AbstractAndroidState {

	private ThreadExpose threadExpose;
	
	public AndroidStateExpose (ThreadExpose threadExpose) {

		this.threadExpose = threadExpose;
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
	public IAndroidState changeState(AndroidEvents androidEvent,
			AndroidStateMachine androidStateMachine) {
			super.changeState(androidEvent, androidStateMachine);
			
			IAndroidState state = this;
			
			
		switch (androidEvent) {
		case STOP_EXPOSE:
			state = getStateMachine().createState(AndroidStates.READY);
			break;
		case STOP_EXPOSE_NO_SERVER:
			state = getStateMachine().createState(AndroidStates.IDLE);
			break; 
		default:
			break;
		}
		
		return state;

	}

	/**
	 * Activate the state.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public void activateState(Handler mainActivityHandler) {
		super.activateState(mainActivityHandler);
		
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
		
		return "Android Expose state";
		
	}
}
