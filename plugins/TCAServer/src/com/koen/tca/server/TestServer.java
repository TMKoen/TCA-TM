package com.koen.tca.server;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.Enumeration;

import com.google.inject.Inject;
import com.koen.tca.server.state.DetectResult;
import com.koen.tca.server.state.ServerEvents;
import com.koen.tca.server.state.ServerStateIdle;
import com.koen.tca.server.state.ServerStateMachine;

/**
 * 'TestServer' is the main class that handles all the necessary actions The RMI
 * methods are used by the client to communicate with the server.
 * 
 * TODO, separate the state machine, so states can be controlled in non RMI
 * mode. (I.e. by command line).
 * 
 * @author Koen Nijmeijer
 * 
 */
public class TestServer extends java.rmi.server.UnicastRemoteObject implements
		IRemoteServer {

	/**
	 * serialVersionUID is needed because TestServer is a serializable class
	 * (because UnicastRemoteObject) The sender and receiver of a serialized
	 * object checks if this number is equal to each other. Otherwise an
	 * InvalidClassException is thrown.
	 */
	private static final long serialVersionUID = 1L;

	// Handles all the States for the server.
	@Inject
	private ServerStateMachine stateMachine;

	// The IP address of the server.
	@SuppressWarnings("unused")
	private String serverAdres;

	// The default RMI server port to communicate with the client.
	private final int serverPort = 1099;

	/**
	 * RMI registry variables
	 **/
	private final String serverRMI = "ServerRMI";
	private Registry registry;

	public TestServer() throws RemoteException {
		super();

		try {

			// This gets the default IP address
			// serverAdres = InetAddress.getLocalHost().toString();
			// System.out.println(serverAdres);

			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();

			for (NetworkInterface networkInterface : Collections
					.list(networkInterfaces)) {

				if (networkInterface.getInetAddresses().hasMoreElements() == true) {
					System.out.println("Display name: "
							+ networkInterface.getDisplayName());
					System.out.println("Name: " + networkInterface.getName());

					Enumeration<InetAddress> inetAddress = networkInterface
							.getInetAddresses();
					for (InetAddress address : Collections.list(inetAddress)) {
						if (address != null) {
							System.out.println("IP: " + address);
						}
					}
				}
			}

		} catch (Exception e) {
			throw new RemoteException("No valid Server-IP adres!");
		}
	}

	public void startServer() {
		try {
			// Create a registry for the server
			registry = LocateRegistry.createRegistry(serverPort);

			// Bind the name and the object to the registry
			registry.rebind(serverRMI, this);

			// Shows message that the server is activated
			System.out.println("Server is started...");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		try {
			registry.unbind(serverRMI);
			unexportObject(this, true);
			unexportObject(registry, true);
			System.out.println("Server is stopped..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized int uploadTestCase(String testSet,
			ObjectOutputStream testCase) throws RemoteException {
		return 0;
	}

	@Override
	public synchronized void startDetect(ICallBackClient callBackClient)
			throws RemoteException {
		stateMachine.changeState(ServerEvents.START_DETECT);
		// stateMachine.activateState();

	}

	@Override
	public synchronized int stopDetect(ICallBackClient callBackClient)
			throws RemoteException {
		if (DetectResult.SINGLETON().getValidUEList().isEmpty()) {
			// No UE's present. The server goes to the 'Idle' state
			stateMachine.changeState(ServerEvents.STOP_DETECT_NO_UE);
		} else {
			// UE's are present. The server goes to the 'Wait' state
			stateMachine.changeState(ServerEvents.STOP_DETECT);
		}
		return 0;
	}

	@Override
	public synchronized void startTestSet(ICallBackClient callBackClient,
			String testSet) throws RemoteException {
		stateMachine.changeState(ServerEvents.START_TEST);
		// stateMachine.activateState();
		System.out.println("Starting test");
	}

	@Override
	public synchronized void startTestCase(ICallBackClient callBackClient,
			String testCase) throws RemoteException {
		stateMachine.changeState(ServerEvents.START_TEST);

		// stateMachine.activateState();
		System.out.println("Starting test");

	}

	@Override
	public synchronized void stopTest(ICallBackClient callBackClient)
			throws RemoteException {
		stateMachine.changeState(ServerEvents.STOP_TEST);
		// stateMachine.activateState();
	}

	@Override
	public synchronized TestResults getTestResults() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String[] getUEList() throws RemoteException {

		DetectResult detectResult = DetectResult.SINGLETON();

		String[] ueList = new String[detectResult.getValidUEList().size()];
		for (int i = 0; i < detectResult.getValidUEList().size(); i++) {
			ueList[i] = detectResult.getValidUEList().get(i).toString();
		}
		return ueList;
	}

	@Override
	public synchronized void getServerStatus() throws RemoteException {
		// TODO Auto-generated method stub
	}

	public ServerStateMachine getStateMachine() {
		return stateMachine;
	}

	/**
	 * change the Ready state to Idle. Other states can't go back to the Idle
	 * state via this method.
	 */
	@Override
	public synchronized void idle() throws RemoteException {
		// get the list with known Android devices.
		DetectResult detectResult = DetectResult.SINGLETON();

		stateMachine.changeState(ServerEvents.IDLE);
		if (stateMachine.getState() instanceof ServerStateIdle) {
			// clear the list.
			detectResult.getValidUEList().clear();
		}
	}
}
