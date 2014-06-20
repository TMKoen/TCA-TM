package com.koen.tca.server.state;


/**
 * Fired when a state should have something to report. 
 * 
 * @author Christophe Bouhier
 */
public interface IStateCallBack {
		
	public void starting();
	
	public void ending();
	
	
}
