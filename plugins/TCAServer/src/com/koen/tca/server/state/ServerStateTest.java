package com.koen.tca.server.state;

import com.google.inject.Inject;
import com.koen.tca.server.DragonXInvoker;

public class ServerStateTest extends AbstractServerState {

	/**
	 * Invoker for the script files. Start the script and gets the actions
	 */
	//@Inject
	private DragonXInvoker dragonXInvoker = new DragonXInvoker();

	public ServerStateTest() {

	}

	@Override
	public IServerState changeState(ServerEvents serverEvent) {

		IServerState state = this;
		
		switch (serverEvent) {

		case STOP_TEST:
		case FINISHED:
			// The Server goes to the 'Wait' state
			state = new ServerStateReady ();
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
		dragonXInvoker.invoke();
	}

	@Override
	public String toString() {
		return "I am currently testing, type tca state details for more information";
	}

	@Override
	public String details() {

		// TODO Read out the invoker
		return super.details();
	}

}
