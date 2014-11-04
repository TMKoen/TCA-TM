package com.koen.tca.server.state;

import com.koen.tca.server.internal.TCAServerActivator;
import com.koen.tca.server.state.IServerState.STATES;

/**
 * Handles the state for the Server.
 * 
 * @version
 * @author Koen Nijmeijer
 * @see IServerState
 * 
 */
public class ServerStateMachine implements IStateCallBack {

	// path and testcases to be invoked by the test state.
	private String path = null;
	private String[] testCases = null;
	
	
	private IServerState presentState;

	public ServerStateMachine() {
		// the Server starts in the 'Idle' state
		setState(createState(STATES.IDLE));
		presentState.activateState(this);
	}

	public void changeState(ServerEvents serverEvent) {

		if (presentState != null) {

			IServerState state = presentState.changeState(serverEvent);

			// check if the state was changed. If not, stay in the present
			// state.
			if (!presentState.equals(state)) {

				// a new IServerState object is created.
				setState (state);

				if (state instanceof ServerStateTest) {
					// set parameters for the test: the path and testcases to be tested
					((ServerStateTest) state).setTestSet(path);
					((ServerStateTest) state).setTestCases(testCases);
				}

				// The presentState is changed, we can now activate it.
				presentState.activateState(this);
			} else {
				// the state wasn't changed because of a wrong ServerEvent.
			}
		} else {
			// The state of the server is floating. NOT GOOD!! It must always
			// Idle, Detect, Ready or Test.

			// Prints the string generated in the toString () method.
			System.out.println(this);
		}
	}



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

	public IServerState createState(STATES requestedState) {

		Class<? extends IServerState> clazzToInstantie = null;

		switch (requestedState) {
		case IDLE:
			clazzToInstantie = ServerStateIdle.class;
			break;
		case DETECT:
			clazzToInstantie = ServerStateDetect.class;
			break;
		case READY:
			clazzToInstantie = ServerStateReady.class;
			break;
		case TEST:
			clazzToInstantie = ServerStateTest.class;
			break;
		}

		if (clazzToInstantie != null) {
			IServerState instance = TCAServerActivator.getInstance()
					.getInjector().getInstance(clazzToInstantie);
			return instance;
		}

		throw new IllegalStateException("Can't process the requested state:"
				+ requestedState);
	}
	
	/**
	 * This parameters are used by the test state to device which path and testcases beeing tested.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param path
	 * @param testCases
	 */
	public void setTestParams (String path, String[] testCases) {
		this.path = path;
		this.testCases = testCases;
	}

}
