package com.koen.tca.server;

public class ServerStateTest implements ServerState {

	public ServerStateTest () {
		
	}

	@Override
	public void ChangeState(ServerEvents serverEvent, ServerStateMachine context) {
		
		switch (serverEvent) {
		
		case STOP_TEST:
		case FINISHED:
			// The Server goes to the 'Wait' state
			context.setState(new ServerStateWait());
			break;
		default:
			// the other serverEvents are not valid in the 'Test' state
			break;
		}
		
	}

	@Override
	public void ActivateState() {
		// TODO Auto-generated method stub
		
	}
}
