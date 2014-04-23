package com.tmobile.tca.States;

public class WaitState implements State {

	public void nextState (StateContext stateContext)
	{
		// The next state from Wait State is the PreTest state.
		stateContext.setState(new PreTestState());
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
