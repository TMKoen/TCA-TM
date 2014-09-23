package com.koen.tca.android.action;

import java.util.Map;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.common.message.RemoteUe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class ActionCALL implements ITestAction {

	private Map <String, String> parameters;
	private Map <String, RemoteUe> ueParameters;
	private ActionRunner actionRunner = ActionRunner.SINGLETON();
	
	@Override
	public synchronized void startTest () {

		PhoneStateListener listener = null;
		TelephonyManager manager = null;
		
		if (actionRunner.getContext() != null) {
			manager = (TelephonyManager) actionRunner.getContext().getSystemService(Context.TELEPHONY_SERVICE);
			
			listener = new PhoneStateListener () {
				
				private boolean called = false;
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
					case TelephonyManager.CALL_STATE_RINGING:
						// waiting for a connection.
						
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						// The called phone has answered the call.
						called = true;
						
						break;
					}
				}
			};

			// Sets the listener/
			manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);	
				
			// Take the phone call.		
			call (actionRunner.getContext());

			try {
				int sleeptime = Integer.parseInt(actionRunner.getAction().getParameters().get("Answertime"));

				
			
			} catch (NumberFormatException e) {
				
			}
			
			
			
			// Stops listening to changed call states.
			manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		}
	}
	
	/**
	 * 
	 * @param context
	 */
	public void call (Context context)
	{
		if (!ueParameters.isEmpty()) {
			// Gets the first RemoteUe. This must be the calling UE.
			Map.Entry<String, RemoteUe> entry = ueParameters.entrySet().iterator().next();

			// Make the Call
			String uri = "tel:" + entry.getValue().getMsisdn();
			Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(uri));
			context.startActivity(intent);		
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
