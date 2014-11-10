package com.koen.tca.server;

import com.koen.tca.common.message.RemoteAction;
import com.koen.tca.common.message.RemoteUe;
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
 * @author Koen Nijmeijer
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

	// The RemoteAction that must be send to the UE.
	private RemoteAction remoteAction = null;

	// The IP address of the UE that must be tested.
	private String ipAddress;
	

	/**
	 * Gets the remoteAction for the present action
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @return the RemoteAction
	 */
	public RemoteAction getRemoteAction() {
		return remoteAction;
	}

	/**
	 * Sets the remoteAction for the present action
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param remoteAction for the present action
	 */
	public void setRemoteAction(RemoteAction remoteAction) {
		this.remoteAction = remoteAction;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	// only called by the method matchUE()
	private void setIPAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSourceImei () {
		String imei = null;
		if (getRemoteAction() != null) {
			if (getRemoteAction().getMap() != null) {
				imei = getRemoteAction().getMap().get("From");
			};
		}
		return imei;
	}
	
	/**
	 * Gets the action name if there is any action.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @return the action name String of the present action
	 */
	public String getActionName() {
		String actionName = null;
		if (getRemoteAction() != null) {
			actionName = getRemoteAction().getActionName();
		}
		return actionName;
	}

	public void setActionName(String actionName) {
		if (getRemoteAction() != null) {
			getRemoteAction().setActionName(actionName);
		}
	}


	@Override
	public void processAction(Action action) {
	}

	@Override
	public void processParameter(Parameter p) {
	}

	@Override
	public void processUE(UE ue) {
	}

	@Override
	public void processMetaObject(UEMetaObject metaObject) {
	}

	@Override
	public void execute() {
		// Checks if the UE is valid
		matchUE();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("...................................\n");
		sb.append("Action:" + this.getActionName() + "\n");
		
		// The IP is detected or not.
		sb.append("IP address of present action: " + (getIpAddress() != null ? getIpAddress() : " No IP address!" ) + "\n");
		return sb.toString();

	}

	/**
	 * Checks it the UE that is referenced in the remoteActions 'from' parameter, exists
	 * in the registered UE list (that was filled in the Detect state).
	 * If there is a match, then ipAddress is set.
	 */
	public void matchUE() {
		
		// A new match is begin, so set the IP address to null.
		setIPAddress(null);
		
		if (remoteAction != null && remoteAction.getMap() != null) {

			
			// Find the UE name that is stored in the ´From´ parameter.
			String ueName = remoteAction.getMap().get("From");

			if (ueName != null && remoteAction.getUeMap() != null) {
				// Get the UE with the same UE name.
				RemoteUe ue = remoteAction.getUeMap().get(ueName);
				if (ue != null && ue.getImei() != null) {
					for (UEInfo info: DetectResult.SINGLETON().getValidUEList()) {
						if (ue.getImei().equalsIgnoreCase(info.getImei())) {
							// if there is a match, then set the IP address
						setIPAddress(info.getIPAddress());
						break;
						}
					}
				}
			}
		}
	}

}