package com.koen.tca.server.state;

/**
 * Handels the state for the Server.
 * 
 * @version
 * @author Koen Nijmeijer
 * @see IServerState
 * 
 */
public class ServerStateMachine implements IStateCallBack {

	private IServerState presentState;

	public ServerStateMachine() {
		// the Server starts in the 'Idle' state
		setState(new ServerStateIdle());
		presentState.activateState(this);
	}

	public void changeState(ServerEvents serverEvent) {
		if (presentState != null) {

			// FIXME, This has side effect of change presentState! Very bad
			// coding practise.
			presentState.changeState(serverEvent);

			// The presentState changed, we can not activate it.
			presentState.activateState(this);
		} else {
			// Prints the string generated in the toString () method.
			System.out.println(this);
		}
	}

	// public void activateState() {
	// presentState.activateState();
	// }

	/**
	 * This method is only used by the ServerState classes to set the new state.
	 * 
	 * @param newState
	 */
	protected void setState(IServerState newState) {
		presentState = newState;
	}

	public IServerState getState() {
		return presentState;
	}

	@Override
	public String toString() {
		return presentState != null ? "state: " + presentState
				: "We should really have a state already!";
	}

	@Override
	public void starting() {
		System.out.println("starting: " + presentState);
		
	}

	@Override
	public void ending() {
		System.out.println("stopping: " + presentState);
	}
}
