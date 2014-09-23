package com.koen.tca.android.action;

import java.util.Map;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.common.message.RemoteUe;

import android.content.Context;
import android.content.Intent;

public class ActionSMS  implements ITestAction {

	private Map <String, String> parameters;
	private Map <String, RemoteUe> ueParameters;
	
	
	public ActionSMS () {
		
	}

	/**
	 * Send a SMS.
	 * @param context
	 */
	public void sendSMS (Context context) {
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.putExtra("sms_body", "default content");
		
		// 
		sendIntent.setType("vnd.android-dir/mms-sms");
		context.startActivity(sendIntent);		
	}

	@Override
	public void startTest() {
		ActionRunner actionRunner = ActionRunner.SINGLETON();

		if (actionRunner.getContext() != null) {
			sendSMS (actionRunner.getContext());
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
