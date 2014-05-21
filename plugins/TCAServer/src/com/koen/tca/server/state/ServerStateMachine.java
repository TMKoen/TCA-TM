package com.koen.tca.server.state;

/**
 * 
 */
public class ServerStateMachine {

	private IServerState presentState;

	public ServerStateMachine() {
		// the Server starts in the 'Idle' state
		setState(new ServerStateIdle());
	}

	public void changeState(ServerEvents serverEvent) {
		if (presentState != null) {
			presentState.changeState(serverEvent, this);
			presentState.activateState();
		} else {
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

}
