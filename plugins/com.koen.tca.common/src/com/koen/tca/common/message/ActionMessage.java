package com.koen.tca.common.message;

import java.io.Serializable;

/**
 * This message holds the remote message for the Android device.
 * <p>
 * It implements the base interface <code>IMessage</code> and
 * the Serializable interface so it can send over a Socket connection. 
 * @version
 * @author Koen Nijmeijer
 * @see IMessage
 *
 */
public class ActionMessage implements IMessage, Serializable {

	// Auto generated version ID, necessary for Serializable.
	private static final long serialVersionUID = -8902377679877573386L;

	// Holds the action that must send to the Android device.
	private RemoteAction action;
	
	public ActionMessage (RemoteAction action) {
		this.action = action;
	}
	
	/**
	 * @version
	 * @author Koen Nijmeijer
	 * @return the remote action object for the Android device.
	 */
	public RemoteAction getAction () {
		return action;
	}
	
	/**
	 * Overrides the <code>toString()</code> method.
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public String toString () {
		return "Action message with " + action.toString();
	}
}
