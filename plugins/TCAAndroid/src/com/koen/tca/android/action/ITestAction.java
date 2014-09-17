package com.koen.tca.android.action;

import java.util.Map;

import com.koen.tca.common.message.RemoteUe;

public interface ITestAction {

	public void setUeParameters (Map <String, RemoteUe> ueParam );
	public void setParameters (Map <String, String> param);
	public void startTest ();
}
