package com.koen.tca.android;

public class AndroidStateMachine {

	private AndroidState androidState;

	public AndroidStateMachine () {
		setState (new AndroidStateIdle());
	}
	
	protected void setState (AndroidState androidState) {
		this.androidState = androidState;
	}
	
	public AndroidState getState () {
		return androidState;
	}
	
	public void changeState (AndroidEvents androidEvent) {
		if (androidState != null) {
			androidState.changeState(androidEvent, this);
		}
	}
	
	public void activateState () {
		if (androidState != null) {
			androidState.activateState();
		}
	}
}
