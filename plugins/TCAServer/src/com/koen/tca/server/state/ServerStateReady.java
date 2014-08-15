package com.koen.tca.server.state;

public class ServerStateReady extends AbstractServerState {

	public ServerStateReady() {

	}

	@Override
	public IServerState changeState(ServerEvents serverEvent) {

		IServerState state = this;

		switch (serverEvent) {
		case START_DETECT:
			// server goes to the 'Detect' state.
			state = getContext().createState(STATES.DETECT);
			break;
		case IDLE:
			// server goes to the 'Idle' state.
			state = getContext().createState(STATES.IDLE);
			break;
		case START_TEST:
			state = getContext().createState(STATES.TEST);
			break;
		default:
			// the other serverEvents are not valid in the 'Ready' state
			break;
		}
		return state;

	}

	@Override
	public String toString() {
		return "I am currently ready..";
	}

}
