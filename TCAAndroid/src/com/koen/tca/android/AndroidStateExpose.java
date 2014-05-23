package com.koen.tca.android;

public class AndroidStateExpose implements AndroidState {

	private ThreadExpose exposeThread;
	
	public AndroidStateExpose () {
		exposeThread = new ServerExpose ();
	}
	
	@Override
	public void changeState(AndroidEvents androidEvent,
			AndroidStateMachine androidStateMachine) {
	
		switch (androidEvent) {
		case STOP_EXPOSE:
			androidStateMachine.setState(new AndroidStateReady());
			break;
		case STOP_EXPOSE_NO_SERVER:
			androidStateMachine.setState(new AndroidStateIdle());
			break; 
		default:
			break;
		}

	}

	@Override
	public void activateState() {
		
		// Start a new Thread that runs the state: Expose
		exposeThread.startThread();

		// Returns to the client while the new thread is running.
	}

	/**
	 * Override the default toString method to return "Expose"
	 */
	@Override
	public String toString () {
		
		return "Expose";
		
	}
}
