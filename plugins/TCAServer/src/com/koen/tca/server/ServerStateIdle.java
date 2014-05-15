package com.koen.tca.server;

public class ServerStateIdle implements ServerState {

	public ServerStateIdle () {
		
	}
	
	@Override
	public void ChangeState(ServerEvents serverEvent, ServerStateMachine context) {
		
		
		switch (serverEvent){
		case START_DETECT:
			context.setState(new ServerStateDetect());
			break;
		default:
			break;
		}
	}

	@Override
	public void ActivateState() {
		
		
	}

	
}
