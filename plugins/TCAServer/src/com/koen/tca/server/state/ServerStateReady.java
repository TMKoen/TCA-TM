package com.koen.tca.server.state;

public class ServerStateReady extends AbstractServerState {

	public ServerStateReady() {

	}

	@Override
	public void changeState(ServerEvents serverEvent) {

		switch (serverEvent) {
		case START_DETECT:
			// server goes to the 'Detect' state.
			getContext().setState(new ServerStateDetect());
			break;
		case IDLE:
			// server goes to the 'Idle' state.
			getContext().setState(new ServerStateIdle());
			break;
		case START_TEST:
			getContext().setState(new ServerStateTest());
			break;
		default:
			// the other serverEvents are not valid in the 'Ready' state
			break;
		}

	}

	@Override
	public String toString() {
		return "I am currently ready...ready for what??";
	}

}
