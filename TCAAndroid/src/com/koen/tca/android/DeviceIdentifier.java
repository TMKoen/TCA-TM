package com.koen.tca.android;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Holds the IMEI and telephone number of the Android Device. 
 * It also holds the server IP address and the port number to access to.
 * <p>
 * This is a Singleton class so there can be only one object. It can be accessed every where 
 * in the code if necessary.
 * @version
 * @author Koen Nijmeijer
 *
 */
public class DeviceIdentifier {

	// Handler to itself. Other objects can use this handler to access this object.
	private static DeviceIdentifier self;
	
	// Holds the IMEI number of the Android device.
	private String imeiNumber;
	
	// Holds the telephone number of the Android device.
	private String telephoneNumber;
	
	// Holds the server IP address and the port number of the connection to the server.
	private int socketPortNumber;
	private String serverIpAddress;
	
	/**
	 * This constructor is only accessible by itself (private).
	 * <p> 
	 * The static SINGLETON method create only one object of this class
	 * @version
	 * @author Koen Nijmeijer
	 */
	private DeviceIdentifier () {
		// only accessible by the class itself		
	
		// Initialize
		imeiNumber = "";
		telephoneNumber = "";
		socketPortNumber = 0;
		serverIpAddress = "";
		
		
	}

	/**
	 * Gets the object of the DeviceIdentifier.
	 * This method ensures that there can be only one instance of 
	 * DeviceIdentifier (a singleton object). So everywhere in the code, 
	 * if someone makes a new object of this class, the get the same instance.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @return the object DeviceIdentifier itself
	 * 
	 */
	public static DeviceIdentifier SINGLETON () {
		if (self == null) {
			self = new DeviceIdentifier ();

		}
		return self;
	}

	/**
	 * Gets the IMEI number of the mobile phone.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @return the IMEI string of the mobile phone
	 */
	public synchronized String getImeiNumber() {
		return imeiNumber;
	}

	/**
	 * sets the IMEI number of the mobile phone.
	 * <p>
	 * this method is only accessible from this class (not public). The class itself
	 * search the right IMEI number of the device.
	 * Because this method is only accessed via the synchronized InitDeviceIdentification(..)
	 * method, this method don't has to by synchronized.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param imeiNumber string of the mobile phone IMEI
	 */
	private void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	/**
	 * Gets the telephone number of the Android device.
	 * @return the telephoneNumber string of the mobile phone
	 */
	public synchronized String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * Sets the telephone number.
	 * <p>
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param telephoneNumber string of the number of the mobile phone
	 * 
	 */
	public synchronized void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/** Initialize the singleton object DeviceIdentifier.
	 * <p>
	 *  This method sets the mobile phone IMEI and telephone number.
	 *  It requires READ_PHONE_STATE permission in the AndroidManifest.xml file. 
	 *  
	 * @version
	 * @author Koen Nijmeijer
	 * @param context which must be the base Context class of the TcaMainActivity class
	 */
	public synchronized void initDeviceIdentification (Context context) {

		// Initialize the TelephonyManager that manage the Android device
		TelephonyManager telephoneManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

		// Not necessary to set it twice, so we block that.
		// getLine1Number() return null if there is no number stored.
		if (getTelephoneNumber() == null || "".equals(getTelephoneNumber())) {
			// works only if the telephone number is stored on the SIM!!
			setTelephoneNumber (telephoneManager.getLine1Number());			
		}
		
		// Not necessary to set ImeiNumber twice! 
		// getDeviceID() return null when there is no IMEI number.
		if (getImeiNumber () == null || "".equals(getImeiNumber())) {
			// getDeviceID() gives null if the device don't have an IMEI number.
			setImeiNumber (telephoneManager.getDeviceId());
		}

	}
	
	/**
	 * Initialize (set) the server IP address and port number to communicate with.
	 * <p>
	 * This method don't check if the server address or port number is valid. 
	 * It assumes that the object that access this method, already checked it.
	 * This method is synchronized, so only one thread at a time can access this method.
	 * @version
	 * @author Koen Nijmeijer
	 * @param serverIpAddress the string ip Address of the server
	 * @param portNumber the string port number of the server
	 */
	public synchronized void initServerCommunication (String serverIpAddress, int portNumber) {
		if (isValidIpAddress (serverIpAddress) == true && isValidPortNumber (portNumber) == true) {
			
			setServerIpAddress (serverIpAddress);
			setSocketPortNumber(portNumber);
		}
	}

	/**
	 * Checks if the IP address of the server is valid.
	 * @version
	 * @author Koen Nijmeijer
	 * @param ipaddr
	 * @return true if its valid, otherwise false.
	 */
	public boolean isValidIpAddress (String ipaddr) {
		// TODO: Check IP-address for 3x '.' and numbers between 0 and 255
		return true;
	}
	
	/**
	 * Checks if the port number is valid (between 1024 and 65536).
	 * @version
	 * @author Koen Nijmeijer
	 * @param port
	 * @return true if the port number is valid. false, if it is not
	 */
	public boolean isValidPortNumber (int port) {

		return (port >=1024 && port < 65536)? true : false;
	}

	/**
	 * Gets the port number of the connection to the server.
	 * @version
	 * @author Koen Nijmeijer
	 * @return the integer port number
	 */
	public synchronized int getSocketPortNumber() {
		return socketPortNumber;
	}

	/**
	 * Sets the port number of the connection with the server.
	 * <p>
	 * This private method is only accessed by InitServerCommunication (..), after it has checked
	 * if the port number is valid.
	 * @version
	 * @author Koen Nijmeijer
	 * @param socketPortNumber
	 */
	private void setSocketPortNumber(int socketPortNumber) {
		this.socketPortNumber = socketPortNumber;
	}

	/**
	 * Gets the server IP address.
	 * This method is synchronized, so only one thread at a time can access it.
	 * @version
	 * @author Koen Nijmeijer
	 * @return the String IP address of the server
	 */
	public synchronized String getServerIpAddress() {
		return serverIpAddress;
	}

	/**
	 * Sets the server IP address.
	 * <p>
	 * This method is private and only accessible by itself.
	 * Because it is accessed via the the synchronized <link>InitServercommunication</link> method,
	 * this method don't has to by synchronized.
	 * @see InitServerCommunication
	 *
	 * @version
	 * @author Koen Nijmeijer
	 * @param serverIpAddress the string IP address of the server
	 */
	private void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
	}
}

