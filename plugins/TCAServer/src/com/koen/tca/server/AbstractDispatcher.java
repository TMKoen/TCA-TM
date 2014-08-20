package com.koen.tca.server;

import com.koen.tca.server.state.DetectResult;
import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.DragonX;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.dragonX.UE;
import com.netxforge.netxtest.dragonX.UEMetaObject;
import com.netxforge.netxtest.interpreter.IExternalDispatcher;

/**
 * Dispatches {@link DragonX} scripts. Client should extend to get base
 * functionality like:
 * <ul>
 * <li>Process the {@link Parameter} of an {@link Action} to extract the
 * {@link UE} objects and store the</li>
 * <li>A basic job which dispatches the action</li>
 * </ul>
 * 
 * @author Christophe Bouhier
 * 
 */
public abstract class AbstractDispatcher implements IExternalDispatcher {

	public static class DEFAULT extends AbstractDispatcher {

		@Override
		public void processAction(Action action) {
			System.out.println("=> Processing Action: " + action);
			super.processAction(action);

		}

		@Override
		public void processParameter(Parameter p) {
			System.out.println("=> Processing Parameter: " + p);
			super.processParameter(p);
		}

		@Override
		public void processUE(UE ue) {
			System.out.println("=> Processing UE: " + ue);
			super.processUE(ue);

		}

		@Override
		public void processMetaObject(UEMetaObject metaObject) {
			System.out.println("=> Processing UEMetaObject: " + metaObject);
			super.processMetaObject(metaObject);
		}

		@Override
		public void execute() {
			super.execute();
			System.out.println("Executing:\n" + this);

		}
	}

	/**
	 * The IMEI numer of a Ue
	 * 
	 * @see {@link http 
	 *      ://en.wikipedia.org/wiki/International_Mobile_Station_Equipment_Identity
	 *      * }
	 */
	private String imeiNumber = null;

	/**
	 * Get the IMEI.
	 * 
	 * @return
	 */
	public String getImeiNumber() {
		return imeiNumber;
	}

	/**
	 * Set the IMEI
	 * 
	 * @param imeiNumber
	 */
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	/**
	 * The MSISDN of a UE
	 * 
	 * @link
	 */
	private String msisdn;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	private String ipAddress;

	public String getIpAddress() {
		return ipAddress;
	}

	private void setIPAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	private String actionName = null;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	/**
	 * The parameter being currently processed.
	 */
	private String currentParameter;

	public String getCurrentParameter() {
		return currentParameter;
	}

	public void setCurrentParameter(String currentParameter) {
		this.currentParameter = currentParameter;
	}

	private String ueName;

	public String getUeName() {
		return ueName;
	}

	public void setUeName(String ueName) {
		this.ueName = ueName;
	}

	@Override
	public void processAction(Action action) {
		this.setActionName(action.getName().toString());
	}

	@Override
	public void processParameter(Parameter p) {
		this.setCurrentParameter(p.getName().toString());
	}

	@Override
	public void processUE(UE ue) {
		// No interrest in the UE Object.
		this.setUeName(ue.getName());
	}

	@Override
	public void processMetaObject(UEMetaObject metaObject) {
		switch (metaObject.getParams()) {
		case IMEI: {
			this.setImeiNumber(metaObject.getParamValue());
		}break;
		case MSIDN: {
			this.setMsisdn(metaObject.getParamValue());
		}break;
		}
	}

	@Override
	public void execute() {
		matchUE();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("...................................\n");
		sb.append("Action:" + this.getActionName() + "\n");
		sb.append("IMEI:" + this.getImeiNumber() + "\n");
		sb.append("MSISDN:" + this.getMsisdn() + "\n");

		// The IP is detected.
		sb.append("IP (Detected):" + (this.getIpAddress() != null ? this
				.getIpAddress() : " No IP detected" ) + "\n");
		return sb.toString();

	}

	public void matchUE() {

		if (this.getImeiNumber() != null) {
			for (UEInfo info : DetectResult.SINGLETON().getValidUEList()) {
				if (getImeiNumber().equalsIgnoreCase(info.getImei())) {
					String ipAddress = info.getIPAddress();
					this.setIPAddress(ipAddress);
				}
			}
		}
	}

}