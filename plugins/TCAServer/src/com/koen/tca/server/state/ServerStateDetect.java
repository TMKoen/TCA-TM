package com.koen.tca.server.state;

import java.io.IOException;

import com.koen.tca.server.thread.AndroidDetector;

/**
 * class that represents the state 'Detect'. It inherent the abstract class
 * <code>AbstractServerState</code> which in turn inherent from the interface
 * <code> IServerState </code>.
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
			// Sets the Server port number to 8888. With this port, Android devices Register itself to te server.
			androidDetector = new AndroidDetector(8888);
			androidDetector.setDetectResult(DetectResult.SINGLETON());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IServerState changeState(ServerEvents serverEvent) {

		IServerState state = this;

		switch (serverEvent) {
		case STOP_DETECT:

			androidDetector.stopThread();

			// Release this resource.. Maybe unnecessary, because when changing
			// States,
			// the old state (this state) is also released.
			androidDetector = null;

			// server goes to the wait state. UE's are present.
			state = getContext().createState(STATES.READY);
			break;
		case STOP_DETECT_NO_UE:

			androidDetector.stopThread();

			// Garbage collected.. Server goes to the 'Idle' state. No UE's are
			// present.
			androidDetector = null;
			state = getContext().createState(STATES.IDLE);
		default:
			// the other serverEvents are not valid in the 'Connect' state
			break;
		}
		return state;
	}

	@Override
	public void activateState(ServerStateMachine context) {
		super.activateState(context);
		androidDetector.startThread(this);

	}

	@Override
	public String toString() {
		return "I am currently detecting test UE's...with my android detector, type tca state details for more information";
	}

	public String details() {
		return androidDetector.toString();
	}

	@Override
	public void ending() {

		// fired by Android Thread being completed, like a Socket timeout etc...
		// Go back to idle mode.
		IServerState serverStateReady = this.getContext().createState(
				STATES.IDLE);
		getContext().setState(serverStateReady);
		serverStateReady.activateState(getContext());

		// Notify upstream we are ending.
		super.ending();
	}

	public AndroidDetector getAndroidDetector() {
		return androidDetector;
	}

}
