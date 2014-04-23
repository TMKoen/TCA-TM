package com.tmobile.tca.States;

public interface State {

	public void nextState (StateContext statecontext);
	public void waitState (StateContext statecontext);
	public void idleState (StateContext statecontext);
	
}
