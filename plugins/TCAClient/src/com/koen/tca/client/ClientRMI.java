package com.koen.tca.client;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientRMI extends UnicastRemoteObject implements ICallBackClient {


	/**
	 * serialVersionUID is needed because ClientRMI is a serializable class
	 * The sender and receiver of a serialized object checks if this number is equeal to each other.
	 * Otherwise an InvalidClassException is thrown.
	 */
	private static final long serialVersionUID = 1L;
	
	private final String serverAddress = "192.168.56.1"; // The server- IP
															// address
	private final int serverPort = 1099; // The Port for the Client-Server
											// communication. By default RMI use
											// 1099
	private final String serverRegistryName = "ServerRMI";

	private IRemoteServer remoteServer;
	private Registry registry;

	
	public ClientRMI() throws RemoteException{

		try {
			// Get the Registry from the Server
			registry = LocateRegistry.getRegistry(serverAddress, serverPort);

			// Finds the remote Server object
			remoteServer = (IRemoteServer) registry.lookup(serverRegistryName);

		} catch (RemoteException | NotBoundException  e) {
			System.out.println("Server is not Registered (bounded) in the registry, or the Server is not online!");
		}
		
	}

	public synchronized void startTestSet() {
		try {
			remoteServer.startTestSet(this, "");
		} catch (RemoteException e) {
			System.out.println("Server is not online!");
		}

	}
	
	public synchronized void startTestCase (String testCase) {
		try {
			remoteServer.StartTestCase(this, testCase);
		} catch (RemoteException e) {
			System.out.println ("Server is not online!");
		}
	}

	public synchronized void stopTest() {
		try {
			remoteServer.stopTest(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public synchronized void uploadTestScript(String testCaseName) {
		try {
			remoteServer.uploadTestCase(testCaseName, null);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void startDetect () {
		try {
			remoteServer.startDetect(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void stopDetect () {
		try {
			remoteServer.StopDetect(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized TestResults getTestResults () {
		TestResults testResults = null;
		try {
			testResults = remoteServer.getTestResults();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return testResults;
	}
	public synchronized void getServerStatus () {
		try {
			remoteServer.getServerStatus();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized String [] getUEList () {
		String [] ueList = null;
		try {
			ueList = remoteServer.getUEList();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return ueList;
	}
	
	@Override
	public void testIsDone() throws RemoteException {

		System.out.println("\nThe test is Finished!\n\n");
		System.out.print("TCA>");		
		try {
			unexportObject (this, true);
		} catch (NoSuchObjectException e) {
			// The RMI runtime thinks we're not a server, so thats fine.
		}
		
	}

	@Override
	public void testFailed(String reason) throws RemoteException {
		System.out.println("\nThe test has failed!\n");
		System.out.println("reason:" + reason + "\n\n");
		System.out.print("TCA>");		
		try {
			unexportObject (this, true);
		} catch (NoSuchObjectException e) {
			// The RMI runtime thinks we're not a server, so thats fine.
		}
		
	}

	@Override
	public void detectFailed(String reason) throws RemoteException {
		System.out.println("\nDetecting for user equipments has failed!\n");
		System.out.println("reason:" + reason + "\n\n");
		System.out.print("TCA>");		
		try {
			unexportObject (this, true);
		} catch (NoSuchObjectException e) {
			// The RMI runtime thinks we're not a server, so thats fine.
		}
	
		
	}

	@Override
	public void detectIsDone(String[] uelist) throws RemoteException {
		System.out.println("\nDetecting for user equipments is finished!\n");
		System.out.println("User equipments found:\n");
		for (String ue : uelist) {
			System.out.println ("\t" + ue + "\n");
		}
		System.out.print("\nTCA>");		

		try {
			unexportObject (this, true);
		} catch (NoSuchObjectException e) {
			// The RMI runtime thinks we're not a server, so thats fine.
		}
		
	}

}
