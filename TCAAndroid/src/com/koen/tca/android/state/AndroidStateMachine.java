package com.koen.tca.android.state;


public class AndroidStateMachine {

	private IAndroidState presentState;

	public AndroidStateMachine () {
		// Initialize the State machine to the first state: the Idle state.
		setState (new AndroidStateIdle());
	}
	
	protected void setState (IAndroidState androidState) {
		this.presentState = androidState;
	}
	
	public IAndroidState getState () {
		return presentState;
	}
	
	public void changeState (AndroidEvents androidEvent) {
		if (presentState != null) {
			presentState.changeState(androidEvent, this);
			presentState.activateState();
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
