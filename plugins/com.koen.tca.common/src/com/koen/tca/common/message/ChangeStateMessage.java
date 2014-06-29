package com.koen.tca.common.message;

import java.io.Serializable;

public class ChangeStateMessage implements IMessage, Serializable {

	// Auto generated version ID, necessary for Serializable.
	private static final long serialVersionUID = -8797112328236317797L;

	// The event for the Android.
	private AndroidEvents event;

	/**
	 * @version
	 * @author Koen Nijmeijer
	 * @param event
	 */
	public ChangeStateMessage(AndroidEvents event) {
		this.event = event;
	}

	/**
	 * Gets the event for the Android device.
	 * @version
	 * @author Koen Nijmeijer
	 * @return the AndroidEvents event
	 */
	public AndroidEvents getEvent() {
		return event;
	}

	/**
	 * Overrides the <code>toString()</code> method.
	 */
	@Override
	public String toString() {
		return "ChangeState_Message";
	}

}
