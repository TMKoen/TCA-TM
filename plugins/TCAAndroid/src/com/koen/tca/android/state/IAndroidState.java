package com.koen.tca.android.state;

import com.koen.tca.common.message.AndroidEvents;

import android.os.Handler;

public interface IAndroidState {


	public IAndroidState changeState (AndroidEvents event, AndroidStateMachine androidStateMachine);
	
	public void activateState (Handler mainActivityHandler);
	

	
}
