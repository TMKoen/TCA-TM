package com.koen.tca.android.state;

public interface IAndroidState {

	
	public void changeState (AndroidEvents event, AndroidStateMachine androidStateMachine);
	
	public void activateState ();
	
}
