package com.koen.tca.android.state;



public class AndroidStateReady implements IAndroidState {

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
		case START_EXPOSE:
			androidStateMachine.setState(new AndroidStateExpose());
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
		
		return "Android Ready";
		
	}
}
