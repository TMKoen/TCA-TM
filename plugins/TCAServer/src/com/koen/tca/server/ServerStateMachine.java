package com.koen.tca.server;

public class ServerStateMachine {

	private ServerState presentState;
	
	public ServerStateMachine () {
		// the Server starts in the 'Idle' state
		setState (new ServerStateIdle ());
	}

	public void ChangeState (ServerEvents serverEvent) {
		if (presentState != null)
			presentState.ChangeState(serverEvent, this);
			
	}
	
	public void ActivateState () {
		presentState.ActivateState();
	}
	
	/**
	 * This method is only used by the ServerState classes to set the new state.
	 * @param newState
	 */
	protected void setState (ServerState newState) {
		presentState = newState;
	}
	
	public ServerState getState () {
		return presentState;
	}
}
