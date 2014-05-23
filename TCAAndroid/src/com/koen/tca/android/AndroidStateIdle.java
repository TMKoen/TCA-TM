package com.koen.tca.android;


public class AndroidStateIdle implements AndroidState {

	@Override
	public void changeState(AndroidEvents androidEvent,
			AndroidStateMachine androidStateMachine) {

		switch (androidEvent){
		case START_EXPOSE:
			androidStateMachine.setState(new AndroidStateExpose());
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
	 * Override the default toString method to return "Idle"
	 */
	@Override
	public String toString () {
		
		return "Idle";
		
	}

}
