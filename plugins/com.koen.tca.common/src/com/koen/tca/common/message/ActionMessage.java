package com.koen.tca.common.message;

public class ActionMessage implements IMessage {

	private RemoteAction action;
	
	public ActionMessage (RemoteAction action) {
		this.action = action;
	}
	
	public RemoteAction getAction () {
		return action;
	}
	
	@Override
	public String toString () {
		return action.toString();
	}
}
