package com.koen.tca.server;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import com.koen.tca.common.message.RemoteAction;
import com.koen.tca.common.message.RemoteUe;
import com.koen.tca.server.state.DetectResult;
import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.ParamType;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.dragonX.UE;
import com.netxforge.netxtest.dragonX.UEMetaObject;
import com.netxforge.netxtest.dragonX.impl.ParamTypeImpl;
import com.netxforge.netxtest.dragonX.impl.UEImpl;

/**
 * A dispatcher which deals with DragonX Test actions.
 */
public class AndroidDispatcher extends AbstractDispatcher {

	// The port number of the Android devices. With this port, the server connects to the Android devices.

	// a list of all the job names that are active.
	private List<String> jobs = null;

	// An action which is executed remotely.
	private RemoteAction remoteAction = null;

	
	public RemoteAction getRemoteAction() {
		return remoteAction;
	}

	public void setRemoteAction(RemoteAction remoteAction) {
		this.remoteAction = remoteAction;
	}

	

	/**
	 * @author Koen Nijmeijer
	 */
	/*
	private final class RemoteMessageJob extends Job {
		private RemoteMessageJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// Make connection with the Android Device

			// Initialize OK status for returning.
			IStatus status = Status.OK_STATUS;
			
			RemoteMessageTransmitter messageTransmitter = new RemoteMessageTransmitter();
			try {
				String t = AndroidDispatcher.this.getIpAddress();
				
//				InetSocketAddress serverAddress = new InetSocketAddress(
//						getIpAddress(), portNumber);

				DetectResult detectResult = DetectResult.SINGLETON();
				
				List<UEInfo> ueList = detectResult.getValidUEList();
				UEInfo ue =  ueList.get(0);
				
				String ip = ue.getIPAddress();
				
				InetSocketAddress serverAddress = new InetSocketAddress (
						"192.168.178.55", portNumber);
				
				// Open the Socket connection
				clientSocket = new Socket();
				clientSocket.connect(serverAddress, socketTimeout);

				// initialize the output stream
				messageTransmitter.setOutputStream(clientSocket
						.getOutputStream());
				
				// Initialize the input stream
				messageTransmitter.setInputStream(clientSocket.getInputStream());

				// Send a Action message to the Android device.
				messageTransmitter.sendMessage(new ActionMessage(remoteAction));

				// gets the Acknowledge message.
				IMessage remoteMsg = messageTransmitter.receiveMessage(clientSocket, receiveMsgTimeout);
				
				if (remoteMsg != null && remoteMsg instanceof AcknowledgeMessage) {
					if (((AcknowledgeMessage)remoteMsg).getAckMessage().equals("No valid Remote Action!")) {
						// The remote action is not valid
						// return a error status.
						status = Status.CANCEL_STATUS;
						
					} else if  (((AcknowledgeMessage)remoteMsg).getAckMessage().equals("Remote action is ok!")) {
						// Send a ChangeState message to the Android device.
						messageTransmitter.sendMessage(new ChangeStateMessage(
								AndroidEvents.START_TEST));		
					}
				}
				
			} catch (IOException e) {
				System.out.println("IOException.. no connection made!");
				e.printStackTrace();
			} finally {
				messageTransmitter.closeOutputStream();
				messageTransmitter.closeInputStream();

				if (clientSocket != null) {
					try {
						clientSocket.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} // end of finally
			return status;
		} // method run (..)
	}

*/
	
	@Override
	public void processAction(Action action) {
		super.processAction(action);
		
		if (action.getActionCode() != null) {
			// A new action so clear the old RemoteAction and make a new one.
			remoteAction = new RemoteAction(getActionName());
		} else if (action.getParameterSet() != null) {
			
		}
		
//		EList<Parameter> e = action.getParameterSet();
//		for (Parameter p: e) {
//			if (p.getName().toString() == "from") {
//		 		setImeiNumber(p.toString());
//			}
//		}
				
	}

	@Override
	public void processParameter(Parameter p) {

		// Gets the list for the parameters
		super.processParameter(p);

		if (remoteAction != null) {
			if (p.getName().toString().equals("From") ) {
				// Get the IP address of the UE that handles the Action.

				
				UEImpl ue = null;
				
				ParamType pt = (ParamTypeImpl) p.getType();
				
				if (p.getType().getUeRef() != null) 
					ue = (UEImpl) p.getType().getUeRef();
				
				if (ue != null) {
					EList<UEMetaObject> eList = ue.getMeta();
				
					for (UEMetaObject e : eList) {
						if (e.getParams().toString().equals("MSISDN") ) {
							remoteAction.getMap().put(p.getName().toString(), e.getParamValue().toString());
							break;
						} else if (e.getParams().toString().equals("IMEI")) {
						
						}
					}
				
				}

			}
			
			
			
		}
		
//		if (p.eIsSet(DragonXPackage.Literals.PARAMETER__TYPE)) {
//			System.out.println(" parameter: " + p.getName() + " value:"
//					+ p.getType());
//			remoteAction.getMap().put(getCurrentParameter(), p.getType().toString());
//		}
	}

	@Override
	public void processUE(UE ue) {
		super.processUE(ue);

		RemoteUe remoteUe = new RemoteUe ();
		
		if (remoteAction != null) {
			remoteUe.setName(ue.getName());

			EList<UEMetaObject> eList = ue.getMeta();
			
			for (UEMetaObject e : eList) {
				
			}
			
			// Put the UE in the remoteAction UE map.
			remoteAction.getUeMap().put (ue.getName().toString(), remoteUe);
		}
		

	}

	/**
	 * Dispatch our {@link RemoteMessageJob}
	 */
//	private void dispatchJob() {
//		Job job = new RemoteMessageJob("ue-" + getImeiNumber()); // new
		// Job
		// sets the priority of the job and start as soon as
		// possible (scheduled).
//		job.setPriority(Job.SHORT);
//		job.schedule();
//	}

	@Override
	public void execute() {
		
		super.execute(); // Match the UE with a detected UE. 
		
		// Clone the UE Parameters, TODO we assume a single Parameter.
		// Otherwise create a map of Parameters in the parent, and populate the
		// remoteUe for each of the entries
		// in the map.
/*
		
		remoteAction.getUeMap().get(this.getCurrentParameter())
				.setImei(this.getImeiNumber());
		remoteAction.getUeMap().get(this.getCurrentParameter())
				.setMsisdn(this.getMsisdn());
		remoteAction.getUeMap().get(this.getCurrentParameter())
				.setName(this.getUeName());
*/
		
		// get the IP address of the UE.
		DetectResult detectResult = DetectResult.SINGLETON();
		List<UEInfo> ueList = detectResult.getValidUEList();
		UEInfo ue =  ueList.get(0);
		
		String ip = ue.getIPAddress();

		
		if (jobs == null) {
			jobs = new ArrayList<String>();
		}

		// give the new Job a name: 'UE<imei number>'
		String jobName = "UE"+ue.getImei();

		while (jobList(jobName, JOBLISTACTION.READ) == true) {
			// wait until the job is stopped. For one UE, there can be only one connection (one action at a time),
			// so the next action must wait for the previous action to finished.
			try {
				wait(1000);
			} catch (InterruptedException e) {
			}
		}

		if (jobList (jobName, JOBLISTACTION.READ) == false) {
			// the name of the job is not in the list anymore.
			
			// add the new Job name in the list
			jobList (jobName, JOBLISTACTION.ADD);
			
			AndroidConnectionJob job =  new AndroidConnectionJob(this);
			
			// start the new job.
			job.dispatchJob ("name", ip, remoteAction);
			
		} else {
			// timeout was occurred.
		}
		
	}
	
	/**
	 * Remove or add the name of a Job to the list of Job names.
	 * It it Thread safe. There can be only one call simultaneously  that change the list.
	 * @param name
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
