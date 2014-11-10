package com.koen.tca.server;


import java.util.ArrayList;
import java.util.List;

import com.koen.tca.common.message.RemoteAction;
import com.koen.tca.common.message.RemoteUe;
import com.koen.tca.server.internal.TCAServerActivator;
import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.dragonX.UE;
import com.netxforge.netxtest.dragonX.UEMetaObject;
import com.netxforge.netxtest.dragonX.UEPARAMS;

/**
 * A dispatcher which deals with DragonX Test actions.
 * DragonXInvoker loops through all the scripts that must be tested. All the actions in those scripts, 
 * are processed and executed in this class.
 * Every Action is going to be run in an own Job thread, so they can be parallel executed.
 * If one UE is already testing, then this class is waiting until that working job thread is finished.
 * 
 * @Version 1.0
 * @author Koen Nijmeijer
 */
public class AndroidDispatcher extends AbstractDispatcher {


	// a list of all the job names that are active.
	private List<String> jobs = null;


	/**
	 * Process a new action. the action name is stored in a new RemoteAction object.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param action the Action that must be processed.
	 */
	@Override
	public void processAction(Action action) {
		super.processAction(action);
		
		if (action.getActionCode() != null) {

			// A new action so clear the old RemoteAction and make a new one.
			// Google dependency injection makes a new RemoteAction object
//			Class<RemoteAction> remoteActionClass = RemoteAction.class;
//			setRemoteAction (TCAServerActivator.getInstance()
//					.getInjector().getInstance(remoteActionClass));		
			setRemoteAction(new RemoteAction(getActionName()));
			
			// Store the action name in the remoteAction.
			getRemoteAction().setActionName(action.getActionCode().toString());			
		}
		
				
	}

	/**
	 * Process one parameter for the present action. The parameter is stored in the remoteAction.
	 * Because an action can rely on an UE (for example: 'from: UE1' or 'to: UE2'), this method also 
	 * stores the referenced UE information in the remoteAction.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param the Parameter that must be stored in the remoteAction
	 */
	@Override
	public void processParameter(Parameter p) {
		super.processParameter(p);

	
		if (getRemoteAction() != null) {
		
			if (p.getName().toString().equalsIgnoreCase("from") || p.getName().toString().equalsIgnoreCase("To") ) {
				if (p.getType().getUeRef() != null) {
					// From and TO must have a reverence to an UE
					
					// Store the parameter in remoteAction
					getRemoteAction().getMap().put(p.getName().toString(), p.getType().getUeRef().getName());

					RemoteUe ue = new RemoteUe();
					ue.setName(p.getType().getUeRef().getName());
					
					// loop to the meta data of the UE
					for (UEMetaObject obj: p.getType().getUeRef().getMeta()) {
						if (obj.getParams() == UEPARAMS.IMEI) {
							ue.setImei(obj.getParamValue());														
						} else if (obj.getParams() == UEPARAMS.MSISDN) {
							ue.setMsisdn(obj.getParamValue());
						}
					}
					// Store the UE into the UeMapin of the remoteAction. If there is already an UE with the same name, 
					// than it is automatically skipped. (there can be only one object name in the same Map).
					getRemoteAction().getUeMap().put(p.getType().getUeRef().getName(), ue);
				}
			} else if (p.getName().toString().equalsIgnoreCase("Duration") ||
					p.getName().toString().equalsIgnoreCase("ResponseTime") ||
					p.getName().toString().equalsIgnoreCase("ServiceDelay") ||
					p.getName().toString().equalsIgnoreCase("OffsetStart") ||
					p.getName().toString().equalsIgnoreCase("OffsetEnd") ||
					p.getName().toString().equalsIgnoreCase("P1") ||
					p.getName().toString().equalsIgnoreCase("P2") ||
					p.getName().toString().equalsIgnoreCase("P3") ||
					p.getName().toString().equalsIgnoreCase("P4") ||
					p.getName().toString().equalsIgnoreCase("P5") ||
					p.getName().toString().equalsIgnoreCase("P6")) {
				
				// Store the parameter in remoteAction
				getRemoteAction().getMap().put(p.getName().toString(), String.valueOf(p.getType().getValue()));
					
			} else if (p.getName().toString().equalsIgnoreCase("Response")) {
				if (p.getType().getResponse() != null) {
					getRemoteAction().getMap().put (p.getName().toString(), p.getType().getResponse().toString());
				}
			} else if (p.getName().toString().equalsIgnoreCase("SMSDirection")) {
				if (p.getType().getSmsDirection() != null) {
					getRemoteAction().getMap().put (p.getName().toString(), p.getType().getSmsDirection().toString());
				}
			} else if (p.getName().toString().equalsIgnoreCase("UssdCode")) {
				if (p.getType().getUssdCode() != null) {
					getRemoteAction().getMap().put (p.getName().toString(), p.getType().getUssdCode().toString());
				}
			} else if (p.getName().toString().equalsIgnoreCase("MixerOption")) {
				if (p.getType().getMixerOption() != null) {
					getRemoteAction().getMap().put (p.getName().toString(), p.getType().getMixerOption().toString());
				} else if (p.getName().toString().equalsIgnoreCase("Data")) {
					getRemoteAction().getMap().put (p.getName().toString(), p.getType().getMessage());
				}	
			}	
		} 
	}

	/**
	 * Process the UE parameters
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param the UE parameter
	 */
	@Override
	public void processUE(UE ue) {
		super.processUE(ue);

		// not used. the process of the UE is handled in the processParameter method

	}


	/**
	 * Execute the action that is stored in remoteAction.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 */
	@Override
	public void execute() {
		
		super.execute(); // Match the UE with a detected UE. 

		if (getIpAddress() != null) {
		
			if (jobs == null) {
				jobs = new ArrayList<String>();
			}
	
			// give the new Job a name: 'UE<imei number>'
			String jobName = "UE"+ getSourceImei();

			// When the UE is dead, then this deadUETimer prevents that the server stays in a loop.
			int deadUETimer = 300;
			while (jobList(jobName, JOBLISTACTION.READ) == true && deadUETimer >=0 ) {
				// wait until the job is stopped. For one UE, there can be only one connection (one action at a time),
				// so the next action must wait for the previous action to finished.
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
	
			if (jobList (jobName, JOBLISTACTION.READ) == false) {
				// the name of the job is not in the list anymore.
				
				// add the new Job name in the list
				jobList (jobName, JOBLISTACTION.ADD);
				
				AndroidConnectionJob job =  new AndroidConnectionJob(this);
				
				// start the new job.
				job.dispatchJob (jobName, getIpAddress(), getRemoteAction());
				
			} else {
				// timeout was occurred.
				// remove the Job from the list.
				jobList (jobName, JOBLISTACTION.REMOVE);
			}
		}
		
	}
	
	/**
	 * Remove or add the name of a Job to the list of Job names.
	 * It it Thread safe. There can be only one call simultaneously  that change the list.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param name the name of the job to add / remove to/from the list
	 * @param isAdding is true if the name must be added. If it is false, it must be removed.
	 */
	
	public enum JOBLISTACTION {ADD, REMOVE, READ};
	
	public synchronized boolean jobList (String name, JOBLISTACTION action) {
		boolean isPresent = true;
		
		switch (action) {
		case ADD:
			jobs.add(name);
			break;
		case REMOVE:
			jobs.remove(name);
			break;
		case READ:
			if (!jobs.contains(name)) {
				isPresent = false;
			}
			break;
		}		
		return isPresent;
	}
}
