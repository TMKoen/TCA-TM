package com.koen.tca.android.action;

import java.util.Date;
import java.util.Map;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.android.receivers.DeliveredSmsReceiver;
import com.koen.tca.android.receivers.IncomingSmsReceiver;
import com.koen.tca.android.receivers.SendingSmsReceiver;
import com.koen.tca.common.message.RemoteSmsInfo;
import com.koen.tca.common.message.RemoteUe;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.telephony.SmsManager;

public class ActionSMS  implements ITestAction {

	private final String DEFAULT_MESSAGE = "T-Mobile default SMS-message";
	private Map <String, String> parameters;
	private Map <String, RemoteUe> ueParameters;
	private ActionRunner actionRunner = ActionRunner.SINGLETON();	
	
	private IncomingSmsReceiver incomingSmsReceiver = new IncomingSmsReceiver(this);
	private SendingSmsReceiver sendingSmsReceiver = new SendingSmsReceiver (this);
	private DeliveredSmsReceiver deliveredSmsReceiver = new DeliveredSmsReceiver (this);
	
	
	private boolean isFinished;
	
	public ActionSMS () {
		this.isFinished = false;
		
	}

	public synchronized void isFinished () {
			this.isFinished = true;
	}

	/**
	 * Start the test. it can send a SMS, receive a SMS or Send EN receive a SMS after each other.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param mainActivityHandler the handler to the TcaMainActiviy.
	 */
	@Override
	public void startTest(Handler mainActivityHandler) {

		// The destination UE where the SMS must be send to.
		String from = null;
		String to = null;
		String source = null;

		// The message to be send. 
		String message = DEFAULT_MESSAGE;

		
		// Find the source UE (this UE).
		if (parameters.get("From") != null) {
			from = (ueParameters.get(parameters.get("From"))).getMsisdn();
		}
		
		// Find the destination UE.
		if (parameters.get("To") != null) {
			to = (ueParameters.get(parameters.get("To"))).getMsisdn();  	
		}

		// source is the UE that sends a SMS to this UE.
		if (parameters.get("Source") != null) {
			source = (ueParameters.get(parameters.get("Source"))).getMsisdn();
		}

		// Find the message or if not found, there is a default message String.
		if (parameters.get("Data") != null) {
			message = parameters.get("Data");
		}
		
		if (actionRunner.getContext() != null) {
			if (parameters.get("Direction") != null) {
				if (parameters.get("Direction").equalsIgnoreCase("Send")) {

					if (to != null) {
						sendSMS (actionRunner.getContext(), from, to, message);
					}
				} else if (parameters.get("Direction").equalsIgnoreCase("Receive")) {
						receiveSMS(actionRunner.getContext(), source);
					
				} else if (parameters.get("Direction").equalsIgnoreCase("SendAndReceive")) {
					// TODO
				}
			}
		}
		
	}
	
	/**
	 * Send a SMS.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param context
	 */
	public void sendSMS (Context context, String from, String to, String message) {

		final String send = "SMS_SEND";
		final String delivered = "SMS_DELIVERED";

		// Store the start time in the results.
		((RemoteSmsInfo) actionRunner.getResults().getActionInfo()).setStartTime(new Date());

		//
		PendingIntent piSend = PendingIntent.getBroadcast(context, 0, new Intent(send), 0);
		PendingIntent piReceived = PendingIntent.getBroadcast(context, 0, new Intent(delivered), 0);

		// Register the receiver for sending a SMS.
		context.registerReceiver(sendingSmsReceiver, new IntentFilter (send));
		context.registerReceiver(deliveredSmsReceiver, new IntentFilter (delivered));
		

		// Sends the SMS
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(to, from, message,piSend, piReceived);


		long elapsedTme = 0;
		long durationInMilliseconds = 0;
		if (parameters.get("Duratation") != null) {
			
			// Gets the duration parameter and change it in seconds.
			try {
				durationInMilliseconds = Integer.valueOf(parameters.get("Duration")) * 1000;
				
				Date start = new Date();
				
				@SuppressWarnings("unused")
				Date end = null;
				
				// Extra safety if the testcase action parameter is between zero and Max value. If not, it skips the waiting time.
				if (durationInMilliseconds > 0 && durationInMilliseconds <= ActionRunner.MAX_WAIT_TIME_IN_MILLISECONDS) {					
					while (((end= new Date()).getTime() - start.getTime()) <durationInMilliseconds) {
						try {
							if (isFinished == true) {
								// Stops the loop because a broadcast receiver has finished.
								elapsedTme = (new Date().getTime() - start.getTime());
								break;
							}
							// sleep 1 second
							Thread.sleep(1000);				
						} catch (InterruptedException e) {
							// catch an interrupt from another process
						}
					}
				}			
				
			} catch (NumberFormatException e) {
				// the Duration parameter was not an integer.
				durationInMilliseconds = 0;
			}
		}

		// Store the end time in the results.
		((RemoteSmsInfo) actionRunner.getResults().getActionInfo()).setEndTime(new Date());

		if (isFinished == true) {
			// Store the time duration in the results.
			((RemoteSmsInfo) actionRunner.getResults().getActionInfo())
				.setElapsedTime(elapsedTme);
		} else {
			// Duration time was finished, so set this time to the results.
			((RemoteSmsInfo) actionRunner.getResults().getActionInfo())
			.setElapsedTime(durationInMilliseconds);
		}
		
		// unregister the receiver.
		context.unregisterReceiver(sendingSmsReceiver);
		context.unregisterReceiver(deliveredSmsReceiver);
		
/*
 * 	This works, but the user must interact to send the message!!
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.setData(Uri.parse("smsto:"));
		sendIntent.setType("vnd.android-dir/mms-sms");
		
		// sets the address and the body content
		sendIntent.putExtra("address", to);
		sendIntent.putExtra("sms_body", message);

		try {
		context.startActivity(sendIntent);		
		} catch (ActivityNotFoundException e) {
			// The SMS is not send
			
		}
*/
	}

	/**
	 * Receive a SMS from an UE referred by the source parameter..
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param context
	 */
	public void receiveSMS (Context context, String source) {
		final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

		// Register the receiver for an SMS received signal from the Telephony manager.
		@SuppressWarnings("unused")
		IntentFilter filter = new IntentFilter(ACTION);
		context.registerReceiver(incomingSmsReceiver, new IntentFilter(ACTION));
		
		long elapsedTme = 0;
		long durationInMilliseconds = 0;

		if (parameters.get("Duration") != null) {
		
//		if (Long.valueOf(parameters.get("Duratation") ) > 0) {
			
			// Gets the duration parameter and change it in seconds.
			try {
				durationInMilliseconds = Long.valueOf(parameters.get("Duration")) * 1000;
				
				Date start = new Date();
				@SuppressWarnings("unused")
				Date end = null;
				
				// Extra safety if the testcase action parameter is between zero and Max value. If not, it skips the waiting time.
				if (durationInMilliseconds > 0 && durationInMilliseconds <= ActionRunner.MAX_WAIT_TIME_IN_MILLISECONDS) {					
					while (((end= new Date()).getTime() - start.getTime()) <durationInMilliseconds) {
						try {
							if (isFinished == true) {
								// Stops the loop because a broadcast receiver has finished.
								elapsedTme = (new Date().getTime() - start.getTime());
								break;
							}
							// sleep 1 second
							Thread.sleep(1000);				
						} catch (InterruptedException e) {
							// catch an interrupt from another process
						}
					}
				}			
				
			} catch (NumberFormatException e) {
				// the Duration parameter was not an integer.
				durationInMilliseconds = 0;
			}
		}

		// Store the end time in the results.
		((RemoteSmsInfo) actionRunner.getResults().getActionInfo()).setEndTime(new Date());

		if (isFinished == true) {
			// Store the time duration in the results.
			((RemoteSmsInfo) actionRunner.getResults().getActionInfo())
				.setElapsedTime(elapsedTme);
		} else {
			// Duration time was finished, so set this time to the results.
			((RemoteSmsInfo) actionRunner.getResults().getActionInfo())
			.setElapsedTime(durationInMilliseconds);
		}

		
		context.unregisterReceiver(incomingSmsReceiver);
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
