package com.koen.tca.server.state;

import com.google.inject.Inject;
import com.koen.tca.server.DragonXInvoker;

public class ServerStateTest extends AbstractServerState {

	/**
	 * Invoker for the script files. Start the script and gets the actions
	 */
	@Inject
	private DragonXInvoker dragonXInvoker;

	public ServerStateTest() {

	}

	@Override
	public void changeState(ServerEvents serverEvent, ServerStateMachine context) {

		switch (serverEvent) {

		case STOP_TEST:
		case FINISHED:
			// The Server goes to the 'Wait' state
			context.setState(new ServerStateReady());
			break;
		default:
			// the other serverEvents are not valid in the 'Test' state
			break;
		}

	}

	@Override
	public void activateState() {
		dragonXInvoker.invoke();
	}
}
