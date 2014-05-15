package com.koen.tca.server;

public class ServerStateWait implements ServerState{

	public ServerStateWait () {
		
	}

	@Override
	public void ChangeState(ServerEvents serverEvent, ServerStateMachine context) {

		switch (serverEvent) {
		case START_DETECT:
			// server goes to the 'Detect' state.
			context.setState(new ServerStateDetect());
			break;
		case IDLE:
			// server goes to the 'Idle' state.
			context.setState(new ServerStateIdle());
			break;
		case START_TEST:
			context.setState(new ServerStateTest());
			break;
		default:
			// the other serverEvents are not valid in the 'Wait' state
			break;
		}
		
	}

	@Override
	public void ActivateState() {
		// TODO Auto-generated method stub
		
	}
	
	
}
