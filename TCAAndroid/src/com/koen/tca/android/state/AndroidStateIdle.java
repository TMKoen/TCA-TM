package com.koen.tca.android.state;

import android.os.Handler;



/**
 * Represents the Android Idle state.
 * <p>
 * It has the following methods:
 * <code>ChangeState(event, androidStateMachine)</code>
 * <code>ActivateState()</code>
 * <code>toString () </code>
 * 
 * @version
 * @author Koen
 * @see changeState(event, androidStateMachine)
 * @see activateState()
 * @see toString()
 *
 */
public class AndroidStateIdle implements IAndroidState {

	@Override
	public void changeState(AndroidEvents event,
			AndroidStateMachine androidStateMachine) {

		switch (event){
		case START_EXPOSE:
			androidStateMachine.setState(new AndroidStateExpose());
			break;
		default:
			break;
		}

	}

	/**
	 * Activate the Idle state.
	 * <p>
	 * @version
	 * @author Koen Nijmeijer
	 * @param mainActivityHandler the message handler of the TCAMainActivity.
	 */
	@Override
	public void activateState(Handler mainActivityHandler) {
		// The Idle state has nothing to do.

	}

	/**
	 * Override the default toString method to return "Android Idle".
	 */
	@Override
	public String toString () {
		
		return "Android Idle";
		
	}

}
