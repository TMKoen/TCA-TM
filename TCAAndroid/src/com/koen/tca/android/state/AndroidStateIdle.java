package com.koen.tca.android.state;




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

	@Override
	public void activateState() {
		// TODO Auto-generated method stub

	}

	/**
	 * Override the default toString method to return "Idle"
	 */
	@Override
	public String toString () {
		
		return "Android Idle";
		
	}

}
