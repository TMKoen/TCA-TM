package com.koen.tca.android;

public class AndroidStateReady implements AndroidState {

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
		default:
			break;
		}

	}

	@Override
	public void activateState() {
		// TODO Auto-generated method stub

	}

	/**
	 * Override the default toString method to return "Ready"
	 */
	@Override
	public String toString () {
		
		return "Ready";
		
	}
}
