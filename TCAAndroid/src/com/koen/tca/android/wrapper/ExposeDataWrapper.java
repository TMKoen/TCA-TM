package com.koen.tca.android.wrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.koen.tca.android.state.AndroidEvents;

public class ExposeDataWrapper {

	public ExposeDataWrapper () {
		
	}
	
	public void SendDeviceInfo (OutputStream outStream, String imei, String phoneNumber) {
		
		try {
			
			BufferedWriter out = new BufferedWriter (new OutputStreamWriter(outStream));
			
			out.write(imei);
			out.write(phoneNumber);
			out.flush();
			if (out != null)
				out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
