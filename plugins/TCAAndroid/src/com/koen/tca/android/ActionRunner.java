package com.koen.tca.android;

import java.util.Map;

import android.content.Context;

import com.koen.tca.android.action.ITestAction;
import com.koen.tca.common.message.RemoteAction;
import com.koen.tca.common.message.RemoteResults;
import com.koen.tca.common.message.RemoteUe;

/**
 * Singleton class that holds the action that de Android device must test.
 * <p>
 * The Android device must handle one test at a time. So to access this action from anywhere
 * in the code, it is made a Singleton class.
 * @version
 * @author Koen Nijmeijer
 *
 */
public class ActionRunner {

	// pointer to itself, so other objects can use this to access this object.
	private static ActionRunner self;

	// Holds the action that the Android device must test.
	private ITestAction action;

	private RemoteResults results;
	
	// The main context (TCAMainActivity) of the Android app.
	private Context context;
	
	// TODO: private ITestResult result;
	
	/**
	 * a private ActionRunner constructor. 
	 * <p>
	 * This constructor can only accessed by the class itself. So other objects can't create an
	 * object from this class. It is a Singleton class where only ONE object can be created.
	 * @version
	 * @author Koen Nijmeijer
	 */
	private ActionRunner () {
		action = null;
		results = null;
	}

	/**
	 * Return the only object of this ActionRunner class. 
	 * <p>
	 * There can by only one object of this class. If it is not earlier created, create the object.
	 * Double checked locking is not necessary because directly by the initialization of the main activity,
	 * this object is created. So no other thread can accessed this earlier.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @return the handler to this object itself
	 */
	public static ActionRunner SINGLETON () {
		if (self == null) {
			self = new ActionRunner ();

		}
		return self;
	}
	
	/**
	 * Set the action that must be test by the Android device.
	 * <p>
	 * This method is synchronized so only one thread can access this method at a time.
	 * The other threads are blocked until the first thread was finished.
	 * @version
	 * @author Koen Nijmeijer
	 * @param action
	 */
	public synchronized void setAction (ITestAction action) {
		this.action = action;
	}
	
	/**
	 * Return the action object that is stored in this singleton object.
	 * @return
	 */
	public synchronized ITestAction getAction () {
		// return the action object. return null if there is no action object.
		return action;
	}
	

	// sets the Context of the Android app. This is the reference point to TCAMainActivity.
	public synchronized void setContext (Context context) {
		this.context = context;
	}
	
	public synchronized Context getContext () {
		return this.context;
	}
	
	/**
	 * Starts the test.
	 * <p>
	 * This method is synchronized so other threads are blocked until the first thread is finished.
	 * @version
	 * @author Koen Nijmeijer
	 */
	public synchronized void startTest() {
		RemoteAction remoteAction = null;
		
		if (action != null) {
			remoteAction = new RemoteAction (action.getClass().getName().toString());

			// Fills the results with the parameters of the action.
			for (Map.Entry<String, String> entry : action.getParameters().entrySet()) {
				remoteAction.getMap().put(entry.getKey(), entry.getValue());
			}
			
			// Fills results with the UE parameters of the action
			for (Map.Entry<String, RemoteUe> entry : action.getUeParameters().entrySet()) {
				remoteAction.getUeMap().put(entry.getKey(), entry.getValue());
			}
			
			// Make a new RemoteResults object with the name: the action class name.
			results = new RemoteResults(action.getClass().getName());
			results.setAction(remoteAction);

			if (getContext () != null) {
				// Store network info before the test.
				results.setNetworkInfoBefore(((TcaMainActivity) getContext()).getNetworkInfo());				
			}

			// Start the test
			action.startTest  ();
			
			if (getContext () != null) {
				// Store network info after the test.
				results.setNetworkInfoAfter(((TcaMainActivity) getContext()).getNetworkInfo());				
			}
		}
	}

	/**
	 * returns the RemoteResults object that holds the results of the test.
	 * @return the RemoteResults results.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 */
	public synchronized RemoteResults getResults () {
		// TODO: multiple copies by multiple threads??
		return results;
	}
	

} // class ActionRunner
