package com.koen.tca.server.state;

import com.koen.tca.server.ICallBackClient;

/**
 * Interface for the server States: Idle, Detect, Ready and Test.
 * The abstract class <code>AbstractServerState</code> implements this interface.
 * <p>
 * The classes <code>ServerStateIdle</code>, <link>ServerStateDetect</link>, <code>ServerStateReady</code> 
 * and <code>ServerStateTest</code> inherent from the <code>AbstractServerState</code> class 
 * <p>
 * This interface has the following methods:
 * <p>
 * {@link #activateState()}<br>
 * {@link #changeState(ServerEvents, ServerStateMachine)} <br>
 * {@link #getCallBack()
 * 
 * @version
 * @author Koen Nijmeijer
 * @see AbstractServerState
 * @see ServerStateIdle
 * @see ServerStateDetect
 * @see ServerStateReady
 * @see ServerStateTest
 */
public interface IServerState {

	/**
	 * Change the state of the object <code>ServerStateMachine</code>.
	 * the state changes, depends of the event that occurs and the present state of the <code>ServerStateMachine</code>.
	 *  
	 * <p>
	 * The possible states are: Idle, Detect, Ready and Test.
	 * 
	 * @param serverEvent represents the event that occurs
	 * @param context holds a handle to the ServerStateMachine
	 * @see ServerEvents
	 * @see ServerStateMachine
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
