package com.koen.tca.common.message;

public class ChangeStateMessage implements IMessage, java.io.Serializable {

	private static final long serialVersionUID = -8797112328236317797L;

	private AndroidEvents event;

	public ChangeStateMessage(AndroidEvents event) {
		this.event = event;
	}

	public AndroidEvents getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return "ChangeState_Message";
	}

}
