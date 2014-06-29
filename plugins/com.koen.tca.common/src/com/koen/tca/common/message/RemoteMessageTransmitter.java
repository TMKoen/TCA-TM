package com.koen.tca.common.message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class RemoteMessageTransmitter {

	// The Object input and output streams for the messages.
	private ObjectOutputStream objOut;
	private ObjectInputStream objIn;
	
	public RemoteMessageTransmitter (InputStream in, OutputStream out) {
		
		setOutputStream (out);
		setInputStream (in);
	}
	
	public RemoteMessageTransmitter () {
		objOut = null;
		objIn = null;
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
	public void sendMessage (IMessage msg) {
		try {
			if (objOut != null) {
				// Send the message.
				objOut.writeObject (msg);

				// clears the buffered output stream
				objOut.flush();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Fetch a message from the ObjectInputStream.
	 * <p>
	 * This method try to receive a message from the Input stream. If a timeout occurs, 
	 * no message was be send. If a timeout is not necessary set it to 0.
	 * @version
	 * @author Koen Nijmeijer
	 * @param socket the Socket for the timeout
	 * @param timeout the integer (in milliseconds) how long must wait for a message
	 * @return the IMessage message, or null if there was any Exception
	 */
	public IMessage receiveMessage (Socket socket, int timeout) {

		IMessage msg = null;

		try {
			socket.setSoTimeout(timeout);
			// read from a null Inputstream give a IOException. So we first checks if its not null.
			if (objIn != null) {
				msg = (IMessage) objIn.readObject();					
			}	
		}
		catch (SocketException e) {
			// IO Exception from setSoTimeout(..)
		}
		catch (SocketTimeoutException e) {
			// a timeout occurs
			// returns null as the message. 
		}
		catch (ClassNotFoundException e) {
		
			e.printStackTrace();
		} 
		catch (StreamCorruptedException e) {
			  // the object on the stream is not serialized!!
			  e.printStackTrace();
		}
		catch (IOException e) {
		e.printStackTrace();
		}

		// return the message, or if there was some exception or timeout: return null;
		return msg;
	}
	
	public ObjectOutputStream getOutputStream () {
		return objOut;
	}
	
	/**
	 * Initialize a ObjectOutputStream from an OutputStream.
	 * @version
	 * @author Koen Nijmeijer
	 * @param out the OutputStream
	 */
	public void setOutputStream (OutputStream out) {

		// if an old output stream is present, close it first.
		if (objOut != null) {
			closeOutputStream ();
		}
		
		try {
			// creates an Buffered OutputStream where we can send hole objects to it.
			objOut = new ObjectOutputStream (new BufferedOutputStream (out));
			
			// clears the buffer. Necessary for the inputstream on the other side of the connection.
			// when flushed, the other side of the connection can initialize the inputstream (read the output stream header).
			objOut.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 
	 * @param in the InputStream to receive messages.
	 * @version
	 * @author Koen Nijmeijer
	 */
	public void setInputStream (InputStream in) {
	
		// If an old inputstream is present, close it first.
		if (objIn != null) {
			closeInputStream ();
		}
		try {
			// creates an Buffered InputStream where we can read hole objects to it.
			objIn = new ObjectInputStream (new BufferedInputStream(in));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * return the ObjectInputStream.
	 * @version
	 * @author Koen Nijmeijer
	 * @return
	 */
	public ObjectInputStream getInputStream () {
		return objIn;
	}

	/**
	 * Close the input stream if it is still open.
	 * @version
	 * @author Koen Nijmeijer
	 */
	public void closeInputStream () {
		if (objIn != null) {
			try {
				objIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Close the output stream, if it is still open.
	 * @version
	 * @author Koen Nijmeijer
	 */
	public void closeOutputStream () {
		if (objOut != null) {
			try {
				objOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
