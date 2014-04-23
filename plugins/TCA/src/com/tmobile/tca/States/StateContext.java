package com.tmobile.tca.States;

public class StateContext {

	// Data members
	private State presentState;			// Handle to the present state 

	
	// Constructors
	public StateContext ()
	{
		setState (new IdleState());
	}
	
	// Methods
	public void setState (State newState)
	{
		this.presentState = newState;
	}
	
	public State getState ()
	{
		return this.presentState;
	}
	
	public void nextState (StateContext stecontext)
	{
		this.presentState.nextState(this);

	}


}
