package com.koen.tca.common.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Mirror for {@link Action} for remote communication.
 */
public class RemoteAction implements java.io.Serializable {

	private static final long serialVersionUID = 4324256759282751201L;

	String actionName = new String();

	/**
	 * Holds the Action parameters.
	 */
	private Map<String, String> map = new HashMap<String, String>();

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public Map<String, String> getMap() {
		return map;
	}

	@Override
	public String toString() {
		return "Remote action: " + actionName + " with  parameters" + map;
	}

}
