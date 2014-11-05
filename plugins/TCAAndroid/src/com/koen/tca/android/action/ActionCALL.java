package com.koen.tca.android.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.common.message.RemoteUe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;


/**
 * Makes a telephone call. After a timeout, the call is canceled.
 * @version 1.0
 * @author Koen
 *
 */
public class ActionCALL implements ITestAction {

	private Map <String, String> parameters;
	private Map <String, RemoteUe> ueParameters;
	private ActionRunner actionRunner = ActionRunner.SINGLETON();
	

	@Override
	public synchronized void startTest (Handler mainActivityHandler) {

		// The time that the call must be waiting before it cancel it.
		long callingTime = 0;

//		PhoneStateListener listener = null;

		// Initialize a Telephony Manager.
		TelephonyManager manager = null;

		if (actionRunner.getContext() != null) {
			manager = (TelephonyManager) actionRunner.getContext().getSystemService(Context.TELEPHONY_SERVICE);

		}
/*
			voicemail = manager.getVoiceMailNumber();
			
*/
		// The message that says witch PhoneSateListener must be activated (on the main UI thread).
		PhoneStateListenerMessages listenerMessage = PhoneStateListenerMessages.LISTEN_CALL;

		// sends the PhoneStateListenerMessages to the main thread (UI thread).
		Message msg = mainActivityHandler.obtainMessage();
		msg.obj = listenerMessage;
		mainActivityHandler.sendMessage(msg);
		
		
/*			
			listener = new PhoneStateListener () {
				
				private boolean called = false;
				public void onCallStateChanged (int state, String incomingNumber) {

					switch (state) {
					case TelephonyManager.CALL_STATE_IDLE:

						if (isOffHooked == true) {
							
							// the present date /time.
							Date presentDate = new Date ();
							
							// The time in seconds from the offHook status until know.
							offHookTime = (int) (presentDate.getTime() - offHookDate.getTime() / 1000);
						}
						
						
						if (called == true) {
							// The connection was made and stopped after that.
							// Maybe the answering machine was made the call.
							called = false;
							
						} else {
							// There was no connection. So the called android was busy or canceling the call.
							
						}
						break;
					case TelephonyManager.CALL_STATE_RINGING:
						// waiting for a connection.
						
						isRinged = true;
						calledMsisdn = incomingNumber;
						
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						// The called phone has answered the call.
						isOffHooked = true;
						offHookDate = new Date ();
						
						break;
					}
				}
			};
			
*/
		
			// Activates the Phone listener.
//			manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);	
				
			// Take the phone call.		
			call (actionRunner.getContext());

			try {
				// Gets the callingTime from the Action class.
				if (parameters.get("callingTime") != null) {
					callingTime = (long) Integer.parseInt(parameters.get("callingTime"));

					// Make answerTime in milliseconds
					callingTime *= 1000;
				}		
			} catch (NumberFormatException e) {
				// Answertime was no number, so answerTime = 0
			}
			
			try {
				
				// Timeout until answerTime is finished or the UI thread stops the timeout.
				this.wait(callingTime);
			} catch (InterruptedException e) {
				// Another Thread wakes up this thread via wakeupState() in the AndroidStateTest class.
				
				
			}
			

			// if the state of the phone is ringing or off hook, hang up the phone call.
			if (manager != null && manager.getCallState() != TelephonyManager.CALL_STATE_IDLE ) {
				// Hang up the phone call
				hangUp (actionRunner.getContext());
			}
			


			// The message that says that the present PhoneSateListener must be stopped (on the main UI thread).
			listenerMessage = PhoneStateListenerMessages.STOP_LISTENING;

			// stops the listener on the main thread (UI thread).
			msg = mainActivityHandler.obtainMessage();
			msg.obj = listenerMessage;
//			mainActivityHandler.sendMessage(msg);
			
			// Stops listening to changed call states.
		//	manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		}
		
/*	}  //if 
 */
	
	/**
	 * makes the call to another phone.
	 *	@version 1.0
	 * @author Koen Nijmeijer 
	 * @param context the Context of the UI thread.
	 */
	public void call (Context context)
	{
		if (parameters.get("to") != null && !ueParameters.isEmpty()) {

			// Get the MsISDN number of the UE to be calling to.
			String number = (ueParameters.get(parameters.get("to"))).getMsisdn();

			// Make the Call
			String uri = "tel:" + number;
			Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(uri));
			context.startActivity(intent);		
		}
		
	}

	/**
	 * Hangs up the phone call.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param context the Context of the UI thread
	 */
	@SuppressWarnings("unchecked")
	public void hangUp (Context context) {
		
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			// Get the ITelephony object
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(tm.getClass().getName());

//			@SuppressWarnings("unchecked")
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			Object telephonyService = m.invoke(tm); // Get the internal ITelephony object

		
			c = Class.forName(telephonyService.getClass().getName()); // Get its class
		
		
//			m = c.getDeclaredMethod("answerRingingCall"); // Get the "endCall()" method
			m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
			m.setAccessible(true); // Make it accessible
			m.invoke(telephonyService); // invoke endCall()
		
		
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		}		
	}

	/**
	 * @version 1.0
	 * @author Koen Nijmeijer
	 */
	@Override
	public void setUeParameters(Map<String, RemoteUe> ueParam) {
		this.ueParameters = ueParam;
		
	}


	/**
	 *  @version 1.0
	 * @author Koen Nijmeijer
	 */
	@Override
	public void setParameters(Map<String, String> param) {
		this.parameters = param;
		
	}


	/**
	 *  @version 1.0
	 * @author Koen Nijmeijer
	 */
	@Override
	public Map<String, RemoteUe> getUeParameters() {
		return this.ueParameters;
	}


	/**
	 *  @version 1.0
	 * @author Koen Nijmeijer
	 */
	@Override
	public Map<String, String> getParameters() {
		return this.parameters;
	}

}
