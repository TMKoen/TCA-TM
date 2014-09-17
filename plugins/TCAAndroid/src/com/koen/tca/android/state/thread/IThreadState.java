package com.koen.tca.android.state.thread;

import android.os.Handler;

/** the Interface for the Android Threads
 * @version 1.0
 * @author Koen
 *
 */
public interface IThreadState extends Runnable {

	public void startThread (Handler mainActivityHandler);
	public void stopThread ();
	
}
