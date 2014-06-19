package com.koen.tca.server.message;

public class MessageAction implements IMessage {

	private IntentAction action;
	
	public MessageAction (IntentAction action) {
		this.action = action;
	}
	
	public MessageAction () {
		
	}
	
	public IntentAction getAction () {
		return action;
	}
	
	@Override
	public String toString () {
		return "Action_Message";
	}
}