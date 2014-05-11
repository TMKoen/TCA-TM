package com.koen.tca.server;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.google.inject.Inject;

public class ServerRMI extends java.rmi.server.UnicastRemoteObject implements
		RmiServerInterface {
	
	@Inject
	DragonXInvoker dragonXInvoker;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String serverAdres;
	private final int serverPort = 1099; // The default RMI server port to
											// communicate with the client

	private final String serverRMI = "ServerRMI";
	private Registry registry;

	public ServerRMI() throws RemoteException {
		super();
		try {
			serverAdres = InetAddress.getLocalHost().toString();
			System.out.println(serverAdres);
		} catch (Exception e) {
			throw new RemoteException("No valid Server-IP adres!");
		}

	}

	public void startTest() {
		System.out.println("Starting test");
		dragonXInvoker.invoke();
	}

	public void stopTest() {

	}

	public void addTestCase(String testCaseName) {

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
	public int UploadTestCase(String testSet, ObjectOutputStream testCase)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void StartScanUE() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int StopScanUE() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startTestSet(String testSet) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StartTestCase(String testCase) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}
