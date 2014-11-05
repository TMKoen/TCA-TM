package com.koen.tca.android.state;

import com.koen.tca.android.state.thread.ThreadExpose;
import com.koen.tca.android.state.thread.ThreadReady;
import com.koen.tca.android.state.thread.ThreadTest;
import com.koen.tca.common.message.AndroidEvents;

import android.os.Handler;

/**
 * Handles the state of the Android device.
 * <p>
 * @version
 * @author Koen Nijmeijer
 *
 */
public class AndroidStateMachine {

	// The present state of the Android device.
	private IAndroidState presentState;

	public AndroidStateMachine () {
		// Initialize the State machine to the first state: the Idle state.
		setState (createState (AndroidStates.IDLE));
	}
	
	/**
	 * sets the state in the AndroidStateMachine. 
	 * <p>
	 * This method is protected and only accessible by the class itself and by
	 * the AndroidState.. classes
	 * @version
	 * @author Koen Nijmeijer
	 * @param androidState
	 */
	private void setState (IAndroidState androidState) {
		if (presentState != androidState) {
			this.presentState = androidState;			
		}

	}
	
	/**
	 * returns the present state. It can be: AndroidStateIdle, AndroidStateExpose, AndroidStateReady or AndroidStateTest.
	 * @version
	 * @author Koen Nijmeijer
	 * @see AndroidStateIdle
	 * @see AndroidStateExpose
	 * @see AndroidStateReady
	 * @see AndroidStateTest
	 * @return an IAndroidState object which is the present state.
	 */
	public IAndroidState getState () {
		return presentState;
	}
	
	/**
	 * Change the state of the Android Device (Idle, Expose, Ready or Test)
	 * <p>
	 * Not always can the state change. It depends on the present state. (for example: if the
	 * present state is Expose, than it can't change to Test).
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param androidEvent
	 * @param mainActivityHandler the message handler of the TcaMainActivity class
	 */
	public void changeState (AndroidEvents androidEvent, Handler mainActivityHandler) {
		if (presentState != null) {

			// Change the state and activates it.
			setState (presentState.changeState(androidEvent, this));

			presentState.activateState(mainActivityHandler);
		}
	}


	/**
	 * creates a new IAndroidState object.After creating, it returns the new object.
	 * @param state the AndroidStates state for the object that must be created
	 * @return the new IAndroidState object
	 * @see IAndroidState
	 */
	public IAndroidState createState (AndroidStates state) {
		IAndroidState newState = null;

		switch (state) {
		case IDLE:
			newState = new AndroidStateIdle ();
			break;
		case EXPOSE:
			newState = new AndroidStateExpose (new ThreadExpose());

			// For testing without the server:
//			newState = new AndroidStateExpose (new ThreadExpose().new TestThreadExpose());
			break;
		case READY:
			newState = new AndroidStateReady (new ThreadReady());
			
			// For testing without the server:
//			newState = new AndroidStateReady (new ThreadReady().new TestThreadReady());
			break;
		case TEST:
			newState = new AndroidStateTest (new ThreadTest());
			break;
		}
		return newState;
	}
	
	/**
	 * return a String message with the present state.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @return the String message
	 */
	@Override
	public String toString () {
		return presentState != null ? "state: " + presentState
				: "We should really have a state already!";
	}

}
