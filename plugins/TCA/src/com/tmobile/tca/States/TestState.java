package com.tmobile.tca.States;

public class TestState implements State {

	public void nextState (StateContext statecontext)
	{
		// The next state of Test state is Final state.
		statecontext.setState(new FinalState());
	}
	
	public void waitState (StateContext statecontext)
	{
		statecontext.setState(new WaitState());
	}
	
	public void idleState (StateContext statecontext)
	{
		statecontext.setState(new IdleState());
	}
}