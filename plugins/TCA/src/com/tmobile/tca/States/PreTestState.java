package com.tmobile.tca.States;

public class PreTestState implements State {

	public void nextState (StateContext stateContext)
	{
		// The next state from PreTest state is the Test state.
		stateContext.setState(new TestState());
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