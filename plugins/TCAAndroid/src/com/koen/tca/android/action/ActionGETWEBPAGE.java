package com.koen.tca.android.action;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.koen.tca.android.ActionRunner;
import com.koen.tca.common.message.RemoteUe;

public class ActionGETWEBPAGE implements ITestAction  {

	private Map<String, RemoteUe> ueParameters;
	private Map<String, String> parameters;
	
	public ActionGETWEBPAGE () {
		
	}

	@Override
	public void startTest() {
		
		ActionRunner actionRunner = ActionRunner.SINGLETON();

		if (actionRunner.getContext() != null) {
			getPage ();
		}
	}
	
	public void getPage () {
		URL url;
		HttpURLConnection urlConnection = null;
		String page = null;

		try {
			url = new URL("http://www.verkeersschoolkoen.nl/index.php");

			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		    page = receiveString(in);
		
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {

		}
		   finally {
		     urlConnection.disconnect();	
		   }
	}

	public String receiveString (InputStream in) {
		StringBuilder sb=new StringBuilder();
        int ch;      

        //-1: end of stream
	    try {
			while ((ch = in.read()) != -1) {
				sb.append((char)ch);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                
	    
		return sb.toString();
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
		return ueParameters;
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}



}
