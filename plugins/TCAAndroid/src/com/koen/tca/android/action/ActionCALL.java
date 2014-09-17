package com.koen.tca.android.action;

import java.util.Map;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.common.message.RemoteUe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class ActionCALL implements ITestAction {

	private Map <String, String> parameters;
	private Map <String, RemoteUe> ueParameters;

	@Override
	public synchronized void startTest () {
		ActionRunner actionRunner = ActionRunner.SINGLETON();

		if (actionRunner.getContext() != null) {
			call (actionRunner.getContext());
		}
	}
	
	
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


	@Override
	public void setUeParameters(Map<String, RemoteUe> ueParam) {
		ueParameters = ueParam;
		
	}


	@Override
	public void setParameters(Map<String, String> param) {
		parameters = param;
		
	}

}
