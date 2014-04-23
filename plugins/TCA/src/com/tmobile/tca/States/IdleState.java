package com.tmobile.tca.States;

public class IdleState implements State {

	public void nextState (StateContext stateContext)
	{
		// The next state from the Idle state is the Wait state.
		this.waitState(stateContext);
	}
	
	public void waitState (StateContext stateContext)
	{
		stateContext.setState(new WaitState());
	}
	
	public void idleState (StateContext stateContext)
	{
		// Empty. The state is already Idle.
		
	}
}
