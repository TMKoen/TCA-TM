package com.koen.tca.server.message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;

import com.koen.tca.server.message.AndroidEvents;

public class RemoteMessageTransmitter {

	public RemoteMessageTransmitter () {
		
	}

	/**
	 * Send a message to the Server via an Buffered Object stream.
	 * <p>
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param msg the IMessage message that must be send. 
	 * @param out the default outputStream to the Server socket
	 */
	public void sendMessage (IMessage msg, OutputStream out) {
		try {
			// creates an Buffered OutputStream where we can send hole objects to it.
			ObjectOutputStream objOut = new ObjectOutputStream (new BufferedOutputStream (out));
			objOut.writeObject (msg);

			// clears the buffered output stream
			objOut.flush();
			if (objOut != null) {
				// close the output stream
				objOut.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public IMessage receiveMessage (InputStream in) {
		IMessage msg = null;
		ObjectInputStream objIn = null;

		// read from a null Inputstream give a IOException. So we first checks if its not null.
		if (in != null) {
			try {
				objIn = new ObjectInputStream (new BufferedInputStream(in));
					msg = (IMessage) objIn.readObject();
					
					// Close the stream.
					if (objIn != null) 
						objIn.close();
					
			}
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (StreamCorruptedException e) {
				  // the object on the stream is not serialized!!
				  e.printStackTrace();
			}
			catch (IOException e) {
			e.printStackTrace();
			}
		}
		return msg;
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
