package com.koen.tca.android.wrapper;

public class MessageExpose implements IMessage {

	private String imei;
	private String telephoneNumber;
	
	public MessageExpose (String emei, String telephoneNumber) {
		this.imei = emei;
		this.telephoneNumber = telephoneNumber;
	}
	
	public String getImei () {
		return imei;
	}
	
	public String getNumber () {
		return telephoneNumber;
	}
	
	@Override
	public String toString () {
		return "Expose_Message";
	}
}
