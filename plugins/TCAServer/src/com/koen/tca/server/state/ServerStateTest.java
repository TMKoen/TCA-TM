package com.koen.tca.server.state;

import com.google.inject.Inject;
import com.koen.tca.server.DragonXInvoker;
import com.koen.tca.server.state.IServerState.STATES;

public class ServerStateTest extends AbstractServerState {

	String testSet = null;
	String [] testCases = null;
	
	/**
	 * Invoker for the script files. Start the script and gets the actions
	 */
	@Inject
	private DragonXInvoker dragonXInvoker;

	public ServerStateTest() {

	}

	@Override
	public IServerState changeState(ServerEvents serverEvent) {

		IServerState state = this;
		
		switch (serverEvent) {

		case STOP_TEST:
		case FINISHED:
			// Stops the thread that is responsible for the handling (validating) of the script actions.
			dragonXInvoker.stopThread();
			// The Server goes to the 'Ready' state
			state = getContext().createState(STATES.READY);
			break;
		default:
			// the other serverEvents are not valid in the 'Test' state
			break;
		}
		return state;

	}

	@Override
	public void activateState(ServerStateMachine context) {
		super.activateState(context);
		dragonXInvoker.invoke(testSet, testCases);
	}

	@Override
	public String toString() {
		return "I am currently testing, type tca state details for more information";
	}

	@Override
	public String details() {

		return "State is Testing\n" + dragonXInvoker.toString();
	}
	
	public void setTestSet (String testSet) {
		this.testSet = testSet;
	}

	public void setTestCases (String [] testCases) {
		this.testCases = testCases;
	}
}
