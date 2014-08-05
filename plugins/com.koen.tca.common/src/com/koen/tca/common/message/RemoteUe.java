package com.koen.tca.common.message;

import java.io.Serializable;

public class RemoteUe implements Serializable {

	// Auto generated version ID, necessary for Serializable.
	private static final long serialVersionUID = 7152057768205929142L;

	// The name of the Android device. It must be the name that is given in the testcase script.
	private String name;
	// The IMEI number of the Android device.
	private String imei;
	// the MSISDN number of the Android device.
	private String msisdn;
	
	// initialize the private data members
	public RemoteUe () {
		name = null;
		imei = null;
		msisdn = null;
	}

	/**
	 * Sets the name of the Android device.
	 * @param name
	 */
	public void setName (String name) {
		this.name = name;
	}
	
	/**
	 * Sets the IMEI number of the Android device.
	 * @param imei
	 */
	public void setImei (String imei) {
		this.imei = imei;
	}
	
	/**
	 * Sets the MSISDN number of the Android device.
	 * @param msisdn
	 */
	public void setMsisdn (String msisdn) {
		this.msisdn = msisdn;
	}
	
	/**
	 * return the name of the Android device that is given in the testcase script file.
	 * @return the name String
	 */
	public String getName () {
		return name;
	}
	
	/**
	 *  returns the IMEI number of the Android device.
	 * @return the IMEI String
	 */
	public String getImei () {
		return imei;
	}
	
	/**
	 * returns the MSISDN number of the Android device.
	 * @return the MSISDN String
	 */
	public String getMsisdn () {
		return msisdn;
	}
	
}
