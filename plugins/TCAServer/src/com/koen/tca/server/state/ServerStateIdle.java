package com.koen.tca.server.state;


public class ServerStateIdle extends AbstractServerState  {

	public ServerStateIdle () {
		
	}
	
	@Override
	public void changeState(ServerEvents serverEvent, ServerStateMachine context) {
		
		
		switch (serverEvent){
		case START_DETECT:
			context.setState(new ServerStateDetect());
			break;
		default:
			break;
		}
	}

	@Override
	public void activateState() {
		
		
	}

	
}
