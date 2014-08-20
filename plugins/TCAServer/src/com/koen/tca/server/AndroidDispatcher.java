package com.koen.tca.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.koen.tca.common.message.ActionMessage;
import com.koen.tca.common.message.AndroidEvents;
import com.koen.tca.common.message.ChangeStateMessage;
import com.koen.tca.common.message.RemoteAction;
import com.koen.tca.common.message.RemoteMessageTransmitter;
import com.koen.tca.common.message.RemoteUe;
import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.DragonXPackage;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.dragonX.UE;

/**
 * A dispatcher which deals with DragonX Test actions.
 */
public class AndroidDispatcher extends AbstractDispatcher {

	private final int portNumber = 8811;

	private final int socketTimeout = 1000 * 60;

	private Socket clientSocket = null;

	// An action which is executed remotely.
	private RemoteAction remoteAction = null;

	public RemoteAction getRemoteAction() {
		return remoteAction;
	}

	public void setRemoteAction(RemoteAction remoteAction) {
		this.remoteAction = remoteAction;
	}

	/**
	 * @author Christophe Bouhier
	 */
	private final class RemoteMessageJob extends Job {
		private RemoteMessageJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// Make connection with the Android Device

			RemoteMessageTransmitter messageTransmitter = new RemoteMessageTransmitter();
			try {
				InetSocketAddress serverAddress = new InetSocketAddress(
						AndroidDispatcher.this.getIpAddress(), portNumber);

				// Open the Socket connection
				clientSocket = new Socket();
				clientSocket.connect(serverAddress, socketTimeout);

				// initialize the output stream
				messageTransmitter.setOutputStream(clientSocket
						.getOutputStream());

				// Send a Action message to the Android device.
				messageTransmitter.sendMessage(new ActionMessage(remoteAction));

				// Send a ChangeState message to the Android device.
				messageTransmitter.sendMessage(new ChangeStateMessage(
						AndroidEvents.START_TEST));

			} catch (IOException e) {
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
			return Status.OK_STATUS;
		} // method run (..)
	}

	@Override
	public void processAction(Action action) {
		super.processAction(action);
		// create a RemoteAction and fill it with the Action.
		remoteAction = new RemoteAction(getActionName());

	}

	@Override
	public void processParameter(Parameter p) {

		// Gets the list for the parameters
		super.processParameter(p);

		if (p.eIsSet(DragonXPackage.Literals.PARAMETER__VALUE)) {
			System.out.println(" parameter: " + p.getName() + " value:"
					+ p.getValue());
			remoteAction.getMap().put(getCurrentParameter(), p.getValue());
		}
	}

	@Override
	public void processUE(UE ue) {
		super.processUE(ue);
		RemoteUe remoteUe = new RemoteUe();
		// put the UE parameter in the remoteMessage
		remoteAction.getUeMap().put(getCurrentParameter(), remoteUe);
	}

	/**
	 * Dispatch our {@link RemoteMessageJob}
	 */
	private void dispatchJob() {
		Job job = new RemoteMessageJob("ue-" + getImeiNumber()); // new
		// Job
		// sets the priority of the job and start as soon as
		// possible (scheduled).
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	@Override
	public void execute() {
		
		super.execute(); // Match the UE with a detected UE. 
		
		// Clone the UE Parameters, TODO we assume a single Parameter.
		// Otherwise create a map of Parameters in the parent, and populate the
		// remoteUe for each of the entries
		// in the map.

		remoteAction.getUeMap().get(this.getCurrentParameter())
				.setImei(this.getImeiNumber());
		remoteAction.getUeMap().get(this.getCurrentParameter())
				.setMsisdn(this.getMsisdn());
		remoteAction.getUeMap().get(this.getCurrentParameter())
				.setName(this.getUeName());

		dispatchJob();
	}

}
