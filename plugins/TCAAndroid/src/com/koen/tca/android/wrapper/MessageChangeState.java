package com.koen.tca.android.wrapper;

import com.koen.tca.android.state.AndroidEvents;

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
