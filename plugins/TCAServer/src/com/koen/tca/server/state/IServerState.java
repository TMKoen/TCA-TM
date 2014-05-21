package com.koen.tca.server.state;

import com.koen.tca.server.ICallBackClient;

/**
 * 
 */
public interface IServerState {

	/**
	 * 
	 * @param serverEvent
	 * @param context
	 */
	public void changeState(ServerEvents serverEvent, ServerStateMachine context);

	/**
	 * 
	 */
	public void activateState();

	/**
	 * The call back method for when the action is completed.
	 * 
	 * @return
	 */
	public ICallBackClient getCallBack();
}
