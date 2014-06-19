package com.koen.tca.android.state.thread;

import android.os.Handler;

public interface IThreadState extends Runnable {

	public void startThread (Handler mainActivityHandler);
	public void stopThread ();
	
}
