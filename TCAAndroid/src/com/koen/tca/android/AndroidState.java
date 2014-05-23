package com.koen.tca.android;

public interface AndroidState {
	public void changeState (AndroidEvents androidEvent, AndroidStateMachine androidStateMachine);
	public void activateState ();
	
	
}
