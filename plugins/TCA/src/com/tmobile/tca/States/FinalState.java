package com.tmobile.tca.States;

public class FinalState implements State {

	public void nextState (StateContext stateContext)
	{
		this.waitState(stateContext);
	}
	
	public void waitState (StateContext stateContext)
	{
		stateContext.setState(new WaitState());		
	}
	
	public void idleState (StateContext stateContext)
	{
		stateContext.setState(new IdleState());
	}
}