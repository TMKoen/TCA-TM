package com.koen.tca.server.message;

import com.koen.tca.server.message.AndroidEvents;

public class MessageChangeState implements IMessage {
	
	private AndroidEvents event;	
	
	public MessageChangeState (AndroidEvents event) {
	
	}
	
	public AndroidEvents getEvent () {
		return event;
	}
	
	@Override
	public String toString () {
		return "ChangeState_Message";
	}


}
