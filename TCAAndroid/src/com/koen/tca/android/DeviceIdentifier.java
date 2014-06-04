package com.koen.tca.android;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceIdentifier {

	private static DeviceIdentifier self;
	
	private String imeiNumber;
	private String telephoneNumber;
	
	private int socketPortNumber;
	private String serverIpAddress;
	
	private DeviceIdentifier () {
		// only accessible by the class itself		
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
	 * gets the IMEI number of the mobile phone.
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @return the IMEI string of the mobile phone.
	 */
	public synchronized String getImeiNumber() {
		return imeiNumber;
	}

	/**
	 * sets the IMEI number of the mobile phone.
	 * <p>
	 * this method is only accessible from this class (not public).
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param imeiNumber string of the mobile phone IMEI.
	 */
	private void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	/**
	 * 
	 * @return the telephoneNumber string of the mobile phone
	 */
	public synchronized String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * Sets the telephone number.
	 * <p>
	 * This method is only accessible by the class itself.
	 * @version
	 * @author Koen Nijmeijer
	 * @param telephoneNumber string of the number of the mobile phone
	 * 
	 */
	private void setTelephoneNumber(String telephoneNumber) {
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

		TelephonyManager telephoneManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

		// Checks if the IMEI or telephone number is already set. Its not
		// necessary to set it twice, so we block that.
		if (getTelephoneNumber() != null || getImeiNumber () != null) {

			// getDeviceID() gives null if the device don't have an IMEI number.
			setImeiNumber (telephoneManager.getDeviceId());
			setTelephoneNumber (telephoneManager.getLine1Number());			
		} else {
			// IMEI and telephone number is already set. Do it again is not necessary.
		}

	}
	
	public synchronized void initServerCommunication (String serverIpAddress, int portNumber) {
		if (isValidIpAddress (serverIpAddress) == true && isValidPortNumber (portNumber) == true) {
			
			setServerIpAddress (serverIpAddress);
			setSocketPortNumber(portNumber);
		}
	}
	
	public boolean isValidIpAddress (String ipaddr) {
		// TODO: Check ipaddress for 3x '.' and numbers between 0 and 255
		return true;
	}
	
	public boolean isValidPortNumber (int port) {

		return (port >=1024 && port < 65536)? true : false;
	}

	public synchronized int getSocketPortNumber() {
		return socketPortNumber;
	}

	private void setSocketPortNumber(int socketPortNumber) {
		this.socketPortNumber = socketPortNumber;
	}

	public synchronized String getServerIpAddress() {
		return serverIpAddress;
	}

	private void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
	}
}

