package com.koen.tca.common.message;

import java.io.Serializable;

/**
 * This message is used as a acknowledge to an earlier message. 
 * <p>
 * This AcknowledgeMessage class implements <code>IMessage</code>. It also
 * implements <code>Serializable</code> interface so it can be send over a socket.
 * @version
 * @author Koen
 * @see IMessage
 */
public class AcknowledgeMessage implements IMessage, Serializable {

	// Auto generated version ID, necessary for Serializable.
	private static final long serialVersionUID = 7490919092877363928L;

	// Message why the Acknowledge is responding to.
	private String message;

	/**
	 * 
	 * @param message
	 */
	public AcknowledgeMessage (String message) {
		setAckMessage(message);
	}

	public AcknowledgeMessage  () {
		// sets an empty string for the message.
		setAckMessage("");
	}
	
	/**
	 * 
	 * @param message
	 */
	public void setAckMessage (String message) {
		this.message = message;
	}
	
	/**
	 * @version
	 * @author Koen Nijmeijer
	 * @return string with the message why the acknowledge is send. 
	 */
	public String getAckMessage () {
		return message;
	}
	
	/**
	 * Overrides the <code>toString</code> method.
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public String toString () {
		return "Acknowledge message: " + message;
	}
}
