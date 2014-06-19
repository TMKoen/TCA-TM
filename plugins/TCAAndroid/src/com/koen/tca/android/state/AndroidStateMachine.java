package com.koen.tca.android.state;

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
		setState (new AndroidStateIdle());
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
	protected void setState (IAndroidState androidState) {
		this.presentState = androidState;
	}
	
	/**
	 * returns a handler to the present state.
	 * @version
	 * @author Koen Nijmeijer
	 * @return
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
			presentState.changeState(androidEvent, this);
			presentState.activateState(mainActivityHandler);
		} else {
			// Prints the String generated from the toString method.
			System.out.println (this);
		}
	}
	
	@Override
	public String toString () {
		return presentState != null ? "state: " + presentState
				: "We should really have a state already!";
	}

}
