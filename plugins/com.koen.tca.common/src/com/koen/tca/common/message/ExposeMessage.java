package com.koen.tca.common.message;

import java.io.Serializable;

public class ExposeMessage implements IMessage, Serializable {

	// Auto generated version ID, necessary for Serializable.
	private static final long serialVersionUID = -7839331262985611234L;
	
	// The  IMEI number of the Android device.
	private String imei;
	
	// The telephone number of the Android device.
	private String telephoneNumber;
	
	public ExposeMessage (String emei, String telephoneNumber) {
		this.imei = emei;
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * Gets the IMEI number of the Android device.
	 * @version
	 * @author Koen Nijmeijer
	 * @return
	 */
	public String getImei () {
		return imei;
	}
	
	/**
	 * Gets the telephone number of the Android device.
	 * @version
	 * @author Koen Nijmeijer
	 * @return
	 */
	public String getNumber () {
		return telephoneNumber;
	}
	
	/** 
	 * Overrides the <code>toString()</code> method.
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public String toString () {
		return "Expose_Message";
	}
}
