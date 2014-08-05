package com.koen.tca.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.koen.tca.common.message.ActionMessage;
import com.koen.tca.common.message.AndroidEvents;
import com.koen.tca.common.message.ChangeStateMessage;
import com.koen.tca.common.message.ExposeMessage;
import com.koen.tca.common.message.RemoteAction;
import com.koen.tca.common.message.RemoteMessageTransmitter;
import com.koen.tca.common.message.RemoteUe;
import com.koen.tca.server.state.DetectResult;
import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.DragonXPackage;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.dragonX.ParameterSet;
import com.netxforge.netxtest.dragonX.UE;
import com.netxforge.netxtest.dragonX.UEMetaObject;
import com.netxforge.netxtest.dragonX.UEPARAMS;
import com.netxforge.netxtest.interpreter.IExternalDispatcher;

/**
 * A dispatcher which deals with DragonX Test actions.
 */
public class AndroidDispatcher implements IExternalDispatcher {

	private final int portNumber = 8811;
	private String ipNumber = null;
	private String imeiNumber = null;
	
	private final int socketTimeout = 1000*60;
	private Socket clientSocket = null;
	private String actionName = null;
	private RemoteAction remoteAction = null;
	
/**
 * Sends the Action to the Android device.
 * <p>
 * 
 * @version
 * @author Christophe Bouhier, Koen Nijmeijer	
 */
	public void dispatch (Action action) {
		
		System.out.println("Dispatching Action: " + action.getName());

		// get the action name.
		actionName = action.getName().toString();
		
		ParameterSet parameterSet = action.getParameterSet();

	//  create a RemoteAction and fill it with the Action.
		remoteAction = new RemoteAction (actionName);

		// Gets the list for the parameters
		Map <String, String> map = remoteAction.getMap();
		Map <String, RemoteUe> ueMap = remoteAction.getUeMap ();
	

		// Put all the parameters in the RemoteMessage object
		for (Parameter p : parameterSet.getParameters()) {

			if (p.eIsSet(DragonXPackage.Literals.PARAMETER__VALUE)) {
				System.out.println(" parameter: " + p.getName() + " value:"
						+ p.getValue());
				map.put(p.getName().toString(), p.getValue());
				
			} else if (p.eIsSet(DragonXPackage.Literals.PARAMETER__UE_REF)) {
				System.out.println(" parameter: " + p.getName() + " UE:"
						+ p.getUeRef());

				// UE Parameters....
				//TODO: copy / paste the new com.koen.tca.common classes.
				RemoteUe remoteUe = new RemoteUe ();
		
				UE ue = p.getUeRef();
				for (UEMetaObject ueObject : ue.getMeta()) {
					System.out.println(" UE param: " + ueObject.getParams()
							+ ueObject.getParamValue());
					
					// fill the remoteUe with values from the action parameter
					if (ueObject.getParams() == UEPARAMS.IMEI) {
						remoteUe.setImei (ueObject.getParamValue());
					} else if (ueObject.getParams() == UEPARAMS.NAME) {
						remoteUe.setName (ueObject.getParamValue());
					} else if (ueObject.getParams() == UEPARAMS.MSIDN) {
						remoteUe.setMsisdn (ueObject.getParamValue());
					}
				}
				// put the UE parameter in the remoteMessage
				ueMap.put (p.getName().toString(), remoteUe);
			}
		} // for
		
		// The IMEI of the UE Action.

		// finds the IMEI of the Android device to run this action.
		//TODO: find the first UE of the ueMap. there is the right ImeiNumber
	
		if (!ueMap.isEmpty()) {
			imeiNumber = ueMap.entrySet().iterator().next().getValue().getImei();
		}
		
		
//		I think this is not needed anymore!! first test
//		for (UEMetaObject meta : ue.getMeta()) {
//			if (meta.getParams() == UEPARAMS.IMEI) {
//				imeiNumber = meta.getParamValue();

//			}

			if (imeiNumber != null) {
				for (UEInfo info : DetectResult.SINGLETON()
						.getValidUEList()) {

					if (imeiNumber.equalsIgnoreCase(info.getImei())) {
						ipNumber = info.getIPAddress();

						Job job = new Job ("ue-" + imeiNumber) {
							@Override
							protected IStatus run (IProgressMonitor monitor) {
								// Make connection with the Android Device
								
								RemoteMessageTransmitter messageTransmitter = new RemoteMessageTransmitter ();
								try {
									InetSocketAddress serverAddress = new InetSocketAddress (ipNumber, portNumber);

									// Open the Socket connection
									clientSocket = new Socket ();
									clientSocket.connect (serverAddress, socketTimeout);

									// initialize the output stream
									messageTransmitter.setOutputStream(clientSocket.getOutputStream());
									
									// Send a Action message to the Android device.
									messageTransmitter.sendMessage(new ActionMessage (remoteAction));
									
									// Send a ChangeState message to the Android device.
									messageTransmitter.sendMessage (new ChangeStateMessage (AndroidEvents.START_TEST));

									
									
								} catch (IOException e) {
									e.printStackTrace();
								}  finally {
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
								return Status.OK_STATUS;
							} // method run (..)	
						}; // new Job ()
						
						// sets the priority of the job and start as soon as possible (scheduled).
						job.setPriority(Job.SHORT);
						job.schedule();
						
					} // if (imeiNumber..)
				} // for
				
			} else {
				System.out.println("NO IMEI MATCH FOR ACTION: "
						+ action);
			}

	//	} // for..
	} // method dispatch ()
} // class AndroidDispatcher
