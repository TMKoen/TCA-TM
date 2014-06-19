package com.koen.tca.server.state;

import java.io.IOException;

import com.koen.tca.server.thread.AndroidDetector;



/**
 * class that represents the state 'Detect'.
 * It inherent the abstract class <code>AbstractServerState</code> which in turn inherent from 
 * the interface <code> IServerState </code>.
 * 
 * @author Koen Nijmeijer
 * @see AbstractServerState
 * @see IServerState
 */
public class ServerStateDetect extends AbstractServerState {

	protected AndroidDetector androidDetector;
	final String threadName = "Detect Thread";
	
	public ServerStateDetect() {
		
		try {
			androidDetector = new AndroidDetector (10);
			androidDetector.setDetectResult(DetectResult.SINGLETON());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void changeState(ServerEvents serverEvent, ServerStateMachine context) {

		switch (serverEvent){
		case STOP_DETECT:
			androidDetector.stopThread();
			androidDetector = null;		// Release this resource.. Maybe unnecessary, because when changing States, the old state (this state) is also released.
			
			// server goes to the wait state. UE's are present.
			context.setState(new ServerStateReady());
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
	public void activateState() {
		// TODO Activate a new Thread. run() is also in the class AndroidDetector.
		androidDetector.startThread();
		androidDetector.setCallBack(getCallBack());
		// Returns to the client while the new thread is running.
	}

	
}
