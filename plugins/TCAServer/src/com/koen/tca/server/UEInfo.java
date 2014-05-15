package com.koen.tca.server;

public class UEInfo {

	private String imei;
	private String name;
	private String number;
	private String ipAddress;
	
	public UEInfo () {
		
	}
	
	public UEInfo (String imei, String name, String number) {
		this.imei = imei;
		this.name = name;
		this.number = number;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public void setIPAddress (String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getIPAddress () {
		return ipAddress;
	}
}
