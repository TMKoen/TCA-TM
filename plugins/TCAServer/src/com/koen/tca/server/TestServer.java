package com.koen.tca.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.koen.tca.server.state.DetectResult;
import com.koen.tca.server.state.ServerEvents;
import com.koen.tca.server.state.ServerStateIdle;
import com.koen.tca.server.state.ServerStateMachine;
import com.koen.tca.server.state.ServerStateTest;

/**
 * 'TestServer' is the main class that handles all the necessary actions The RMI
 * methods are used by the client to communicate with the server.
 * 
 * @version 1.0 
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

	// the call back pointer to the client RMI- computer
	// (the class that called one of the methods of TestServer).
	ICallBackClient callBackClient;
	
	// the in-memory parameters from the serverinit.ini file
	private Map<String, String> initParams;
			
	// The initializing file
	private final String INITFILE = "serverinit.ini";
	
	// If the serverinit.ini file can't be read, then this is the default path to the testSets.
	private final String DEFAULTTESTPATH = "c:/testscript/";
	
	// The directory where the scripts are stored.
	// It is read from the serverinit.ini file but if that file is not present, then use this default value.
	private String testPath = DEFAULTTESTPATH;
	
	// Handles all the States for the server.
	@Inject
	private ServerStateMachine stateMachine;

	// The default RMI server port to communicate with the client.
	private final int serverPort = 1099;

	/**
	 * RMI registry variables
	 **/
	private final String serverRMI = "ServerRMI";
	private Registry registry;

	// Constructor
	public TestServer() throws RemoteException {
		super();

		// Initialize the map that holds the parameters from the serverinit.ini file
		initParams = new HashMap<String, String>();
		
		// Try reading the serverinit.ini file.
		try {
			BufferedReader br = new BufferedReader(new FileReader(INITFILE));

			String key, value;
			String line;
			
			// loop through all the lines in the serverinit.ini file.
			while ((line = br.readLine()) != null) {
				
				// removes the lines that begins with: //
				if (!line.matches("^//")) {
					
					// any valid line must have a ':' between the key and the value. If not? skip that line.
					if ((line.indexOf(':')) != -1) {
						
						// key is the text before the ':'.
						key = line.substring(0, line.indexOf(':'));
						
						// value is the text after the ':' without whites paces at the beginning of the substring.
						value = (line.substring(line.indexOf(':')+ 1)).replaceAll("^\\s+","");
						
						// saves the key/value pair.
						initParams.put(key, value);
					}
				}
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			// no serverini.ini file, so use default directory
			// so testPath = DEFAULTTESTPATH
		} catch (IOException e) {
			
		}

		// sets the testPath to the parameter from the serverinit.ini file
		if (initParams.get("path") != null)
				testPath = initParams.get("path");
		
		
	}

	public void startServer() {
		try {
			// Create a registry for the server
			registry = LocateRegistry.createRegistry(serverPort);

			// Bind the name and the object to the registry
			registry.rebind(serverRMI, this);

			
			// Find IP address of the server. Because a host can hava many IP address, all the
			// possible address are displayed.
			try {
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
						.getNetworkInterfaces();

				// loop through all the network interfaces on the server machine
				for (NetworkInterface networkInterface : Collections
						.list(networkInterfaces)) {				
					if (networkInterface.getInetAddresses().hasMoreElements() == true) {
						Enumeration<InetAddress> inetAddress = networkInterface
								.getInetAddresses();
						
						// Loop through all the InetAdress of that network interface (the can have more IP addresses)
						for (InetAddress address : Collections.list(inetAddress)) {
							
							// filter unused IP address
							if (address != null && 
									!address.isLoopbackAddress() && 
									!address.isMulticastAddress() && 
									!address.isMCGlobal() &&
									!address.isMCLinkLocal() &&
									!address.isMCNodeLocal() &&
									!address.isMCOrgLocal() &&						
									!address.isLinkLocalAddress() ){
								
								// Show the IP address and the networkInterface name.
								System.out.println("IP: " + address+ " (" + networkInterface.getDisplayName()  + ")\n");
							}
						}
					}
				}						
			} catch (Exception e) {
				throw new RemoteException("The server have no valid Server-IP address! ");
			}
			
			// Shows message that the server is activated
			System.out.println("Server is started...");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		try {
			// Unregister the RMI methods
			registry.unbind(serverRMI);
			unexportObject(this, true);
			unexportObject(registry, true);

			System.out.println("Server is stopped..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets the CallBackClient parameter Thread safe (synchronized).
	 * When for example startDetect and stopDetect are called from two different client computers,
	 * than the can't change callBackClient directly (not thread safe). So they use this method.
	 * @param callBackClient
	 */
	private synchronized void setCallBackClient (ICallBackClient callBackClient) {
	if (callBackClient != null)
		this.callBackClient = callBackClient;
	}
	
	@Override
	public synchronized int uploadTestCase(String testSet,
			ObjectOutputStream testCase) throws RemoteException {
		return 0;
	}

	@Override
	public synchronized void startDetect(ICallBackClient callBackClient)
			throws RemoteException {
		
		// Sets the callBackClient where to call back to.
		setCallBackClient (callBackClient);
		
		// Change the state to Detect.
		stateMachine.changeState(ServerEvents.START_DETECT);
	}

	@Override
	public synchronized int stopDetect(ICallBackClient callBackClient)
			throws RemoteException {
		
		// Sets the callBackClient where to call back to.
		setCallBackClient (callBackClient);
		
		if (DetectResult.SINGLETON().getValidUEList().isEmpty())
			// No UE's present. The server goes to the 'Idle' state
			stateMachine.changeState(ServerEvents.STOP_DETECT_NO_UE);
		else
			// UE's are present. The server goes to the 'Wait' state
			stateMachine.changeState(ServerEvents.STOP_DETECT);
		return 0;
	}

	@Override
	public synchronized void startTestSet(ICallBackClient callBackClient,
			String testSet) throws RemoteException {

		// Sets the callBackClient where to call back to.
		setCallBackClient (callBackClient);
		
		// Set the test parameters: the testSet to by tested.
		stateMachine.setTestParams(testPath + testSet, null);
		
		stateMachine.changeState(ServerEvents.START_TEST);
		// stateMachine.activateState();
		System.out.println("Starting testSet: " + testSet);
	}

	@Override
	public synchronized void startTestCase(ICallBackClient callBackClient,
			String testSet, String[] testCases) throws RemoteException {
		
		// Sets the callBackClient where to call back to.
		setCallBackClient (callBackClient);
		
		// Set the test parameters: the testSet and all the TestCases.
		stateMachine.setTestParams(testPath + testSet, testCases);
		
		stateMachine.changeState(ServerEvents.START_TEST);

		System.out.println("Starting testCases: " + testCases.toString() + " in testSet: " + testSet + "\n");

	}

	@Override
	public synchronized void stopTest(ICallBackClient callBackClient)
			throws RemoteException {
		
		// Sets the callBackClient where to call back to.
		setCallBackClient (callBackClient);
		
		stateMachine.changeState(ServerEvents.STOP_TEST);
		// stateMachine.activateState();
	}

	@Override
	public synchronized TestResults getTestResults() throws RemoteException {
		
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
		
	}

	/**
	 * Gets the stateMachine that hold the current state.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @return the stateMachine
	 */
	public ServerStateMachine getStateMachine() {
		return stateMachine;
	}

	/**
	 * change the Ready state to Idle. Other states can't go back to the Idle
	 * state via this method.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 */
	@Override
	public synchronized void idle() throws RemoteException {
		// get the list with known Android devices.
		DetectResult detectResult = DetectResult.SINGLETON();

		stateMachine.changeState(ServerEvents.IDLE);
		if (stateMachine.getState() instanceof ServerStateIdle) {
			// clear the  UE list.
			detectResult.getValidUEList().clear();
		}
	}
}
