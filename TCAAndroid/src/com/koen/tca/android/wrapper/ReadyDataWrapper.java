package com.koen.tca.android.wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.koen.tca.android.state.AndroidEvents;

public class ReadyDataWrapper {

	public ReadyDataWrapper ()
	{
		
	}
	
	public AndroidEvents receiveEvent (InputStream inStream) {	
		BufferedReader in = new BufferedReader (new InputStreamReader(inStream));

		AndroidEvents event = AndroidEvents.NO_EVENT;
		String eventString;
		try {
			if ((eventString = in.readLine()) != null) {
				try {
					event = AndroidEvents.valueOf(eventString);
				} catch (IllegalArgumentException e) {
					// value is not valid. return: NO_EVENT
				}
			}
			if (in != null) 
				in.close();
		} catch (IOException e) {
			e.printStackTrace();		
		}
		return event;
	}
	

}
