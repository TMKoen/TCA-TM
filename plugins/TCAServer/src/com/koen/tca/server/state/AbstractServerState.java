package com.koen.tca.server.state;


/**
 * Superclass of the ServerState classes. this Abstract class is the Superclass
 * for the <code>ServerStateIdle</code>, <code>ServerStateDetect</code>,
 * <code>ServerStateReady</code> and <code>ServerStateTest</code> classes.
 * <p>
 * The class implements <code>IServerState</code> and has the following methods:
 * <br>
 * {@link #getCallBack()}
 * 
 * @version
 * @author Koen Nijmeijer
 * @see IServerState
 */
public abstract class AbstractServerState implements IServerState,
		IStateCallBack {

	/**
	 * Our state machine.
	 */
	private ServerStateMachine context;

	@Override
	public String details() {
		return "Implementators should also override this method and provide it's own details";
	}

	@Override
	public void activateState(ServerStateMachine context) {
		this.context = context;
	}

	public ServerStateMachine getContext() {
		return context;
	}

	@Override
	public void starting() {
		context.starting();
	}

	@Override
	public void ending() {
		context.ending();
	}
}
