package com.koen.tca.android.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.common.message.RemoteUe;

public class ActionINCOMINGCALL implements ITestAction  {

	private Map <String, String> parameters;
	private Map <String, RemoteUe> ueParameters;
	private ActionRunner actionRunner = ActionRunner.SINGLETON();
	

	@Override
	public void startTest() {

		PhoneStateListener listener = null;
		TelephonyManager manager = null;
		
		if (actionRunner.getContext() != null) {
			manager = (TelephonyManager) actionRunner.getContext().getSystemService(Context.TELEPHONY_SERVICE);
			
			listener = new PhoneStateListener () {
				
				private boolean called = false;
				
				@SuppressWarnings("unchecked")
				public void onCallStateChanged (int state, String incomingNumber) {

					switch (state) {
					case TelephonyManager.CALL_STATE_IDLE:
						if (called == true) {
							// The connection was made and stopped after that.
							// Maybe the answering machine was made the call.
							called = false;
							
						} else {
							// There was no connection. So the called android was busy or canceling the call.
							
						}
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						// The called phone has answered the call.
						called = true;
						break;
					case TelephonyManager.CALL_STATE_RINGING:
						// waiting for a connection.
						
						try {
						    Thread.sleep(5000);                 //1000 milliseconds is one second.
						} catch(InterruptedException ex) {
						    Thread.currentThread().interrupt();
						}
						try {
							TelephonyManager tm = (TelephonyManager) actionRunner.getContext().getSystemService(Context.TELEPHONY_SERVICE);

							// Get the ITelephony object
							@SuppressWarnings("rawtypes")
							Class c = Class.forName(tm.getClass().getName());

							@SuppressWarnings("unchecked")
							Method m = c.getDeclaredMethod("getITelephony");
							m.setAccessible(true);
							Object telephonyService = m.invoke(tm); // Get the internal ITelephony object

							
							c = Class.forName(telephonyService.getClass().getName()); // Get its class

							
							// simulate that there is an head set connected and the user push on the head set button.
							Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
							i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
							            KeyEvent.KEYCODE_HEADSETHOOK));
							actionRunner.getContext().sendOrderedBroadcast(i, null);
							
							
//							m = c.getDeclaredMethod("silenceRinger");
//							m.setAccessible(true);
//							m.invoke(telephonyService);
							
							m = c.getDeclaredMethod("answerRingingCall"); // Get the "endCall()" method
//							m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
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
						break;
					}
				}
			};
		}
		
	}
	
	@Override
	public void setUeParameters(Map<String, RemoteUe> ueParam) {
		this.ueParameters = ueParam;
		
	}

	@Override
	public void setParameters(Map<String, String> param) {
		this.parameters = param;
		
	}

	@Override
	public Map<String, RemoteUe> getUeParameters() {

		return this.ueParameters;
	}

	@Override
	public Map<String, String> getParameters() {

		return this.parameters;
	}


}
