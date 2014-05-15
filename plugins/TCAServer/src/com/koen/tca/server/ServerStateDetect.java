package com.koen.tca.server;

import java.io.IOException;

public class ServerStateDetect implements ServerState {

	protected AndroidDetector androidDetector;
	final String threadName = "Detect Thread";
	
	public ServerStateDetect () {
		try {
			androidDetector = new AndroidDetector (10);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ChangeState(ServerEvents serverEvent, ServerStateMachine context) {

		switch (serverEvent){
		case STOP_DETECT:
			androidDetector.stopThread();
			androidDetector = null;		// Release this resource.. Maybe unnecessary, because when changing States, the old state (this state) is also released.
			
			// server goes to the wait state. UE's are present.
			context.setState(new ServerStateWait());
			break;
		case STOP_DETECT_NO_UE:
			
			androidDetector.stopThread();
			androidDetector = null;			// Garbage collected..
			// server goes to the 'Idle' state. No UE's are present.
			context.setState(new ServerStateIdle());
		default:
			// the other serverEvents are not valid in the 'Connect' state
			break;
		}
		
	}

	@Override
	public void ActivateState() {
		// TODO Activate a new Thread. run() is also in the class AndroidDetector.
		androidDetector.startThread();
	
		// Returns to the client while the new thread is running.
	}
}
