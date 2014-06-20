package com.koen.tca.server.state;


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
	 * Change the state of the object <code>ServerStateMachine</code>. the state
	 * changes, depends of the event that occurs and the present state of the
	 * <code>ServerStateMachine</code>.
	 * 
	 * <p>
	 * The possible states are: Idle, Detect, Ready and Test.
	 * 
	 * @param serverEvent
	 *            represents the event that occurs
	 * @see ServerEvents
	 */
	public void changeState(ServerEvents serverEvent);

	/**
	 * Get detailed information about the s
	 * 
	 * @return Details information about this {@link IServerState}
	 */
	public String details();

	/**
	 * Activate this state.
	 * 
	 * @param context
	 * @see ServerStateMachine
	 */
	public void activateState(ServerStateMachine context);

}
