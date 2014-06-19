package com.koen.tca.server.state;

import com.koen.tca.server.ICallBackClient;

/**
 * Superclass of the ServerState classes.
 * this Abstract class is the Superclass for the <code>ServerStateIdle</code>, 
 * <code>ServerStateDetect</code>, <code>ServerStateReady</code> and <code>ServerStateTest</code> classes.
 * <p>
 * The class implements <code>IServerState</code> and has the following methods:<br>
 * {@link #getCallBack()}
 * 
 * @version
 * @author Koen Nijmeijer
 * @see IServerState
 */
public abstract class AbstractServerState implements IServerState {

	private ICallBackClient callBack;

	/**
	 * Gets the handler of the ICallBackClient.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @return callBackclient handler
	 */
	@Override
	public ICallBackClient getCallBack() {
		return callBack;
	}

}
