package com.koen.tca.server.state;

/**
 * Handles the state for the Server.
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

			IServerState state = presentState.changeState(serverEvent);

			// check if the state was changed. If not, stay in the present state.
			if (!presentState.equals(state)) {

				// a new IServerState object is created.
				presentState = state;

				// The presentState is changed, we can now activate it.
				presentState.activateState(this);				
			} else {
				// the state wasn't changed because of a wrong ServerEvent.
			}
		} else {
			// The state of the server is floating. NOT GOOD!! It must always Idle, Detect, Ready or Test. 
			
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
