package com.koen.tca.android;

public class AndroidStateTest implements AndroidState {

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
	public void activateState() {
		// TODO Auto-generated method stub

	}

	/**
	 * Override the default toString method to return "Test"
	 */
	@Override
	public String toString () {
		
		return "Test";
		
	}
}
