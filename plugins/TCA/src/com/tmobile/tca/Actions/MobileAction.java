package com.tmobile.tca.Actions;

public abstract class MobileAction {

	
	//Data members
	WaitType waitBeforAction;
	WaitType waitAfterAction;
	
	// Constructors
	public MobileAction ()
	{
		waitBeforAction = WaitType.NO_WAIT;
		waitAfterAction = WaitType.NO_WAIT;
	}
	
	// Methods
	abstract public void StartAction ();
	
}
