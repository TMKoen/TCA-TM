package com.koen.tca.common.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Mirror for <code>Action</code> class, for remote communication.
 * <p>
 * @version
 * @author Koen Nijmeijer
 * @see Action
 */
public class RemoteAction implements Serializable {

	// Auto generated version ID, necessary for Serializable.
	private static final long serialVersionUID = 4324256759282751201L;

	// The name of the Action.
	String actionName;

	 // Holds the Action parameters.
	private Map<String, String> map;
	
	public RemoteAction (String actionName) {

		// Sets the action name.
		setActionName (actionName);

		// Initialize the map that holds the Action parameters.
		map = new HashMap<String, String>();
	}
	
	/**
	 * Gets the action name.
	 * <p>
	 * @version
	 * @author Koen Nijmeijer
	 * @return
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * Sets the action name.
	 * <p>
	 * @version
	 * @author Koen Nijmeijer
	 * @param actionName
	 */
	public void setActionName(String actionName) {
		if (actionName == null) {
			actionName = new String ();
		}
		this.actionName = actionName;
	}

	/**
	 * gets the map with the Action parameters.
	 * <p>
	 * @version
	 * @author Koen Nijmeijer
	 * @return
	 */
	public Map<String, String> getMap() {
		return map;
	}

	/**
	 * Overrides <code>toString()</code> method.
	 * @version
	 * @author Koen Nijmeijer
	 */
	@Override
	public String toString() {
		return "Remote action: " + actionName + " with  parameters" + map;
	}

}