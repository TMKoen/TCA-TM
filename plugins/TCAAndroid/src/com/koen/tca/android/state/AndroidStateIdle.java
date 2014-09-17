package com.koen.tca.android.state;

import com.koen.tca.common.message.AndroidEvents;

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
public class AndroidStateIdle extends AbstractAndroidState {

	/**
	 * Change the state of the Android. If the event is not valid for the Idle state, it returns itself.
	 * @version: 1.0
	 * @author Koen Nijmeijer
	 * @param event The AndroidEvents event that change the state.
	 * @param androidStateMachine the AndroidStateMachine to call back.
	 * 
	 */
	@Override
	public IAndroidState changeState(AndroidEvents event,
			AndroidStateMachine androidStateMachine) {
		super.changeState(event, androidStateMachine);
		
		IAndroidState state = this;
		switch (event){
		case START_EXPOSE:
			state = getStateMachine().createState(AndroidStates.EXPOSE);
			break;
		default:
			break;
		}
		return state;

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
		
		return "Android Idle state";
		
	}

}
