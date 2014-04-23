import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*; 

import com.netxforge.netxtest.DragonXStandaloneSetup;


public class ServerRMI extends java.rmi.server.UnicastRemoteObject implements RmiServerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Data members
	private String serverAdres;
	private final int serverPort = 1099; // The default RMI server port to communicate with the client

	private final String serverRMI = "ServerRMI";
	private Registry registry;

	// Constructors
	public ServerRMI () throws RemoteException
	{	
		super ();
		try {
			serverAdres = InetAddress.getLocalHost().toString();
			System.out.println (serverAdres);
		}
		catch (Exception e)
		{
			throw new RemoteException ("No valid Server-IP adres!");
		}
		
	}
	
	// Methods
	public void StartTest ()
	{
		System.out.println("Heel goed");
		DragonXStandaloneSetup.doSetup();
		
	}
	
	public void StopTest ()
	{
		
	}
	
	public void AddTestCase (String testCaseName)
	{
		
	}
	
	public void StartServer ()
	{
		try {
			// Create a registry for the server
			registry = LocateRegistry.createRegistry(serverPort);
			
			// Bind the name and the object to the registry
			registry.rebind(serverRMI, this);
			
			// Shows message that the server is activated
			System.out.println("Server is started...");
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
	
	public void StopServer ()
	{
		try 
		{
			registry.unbind(serverRMI);
			unexportObject (this,true);
			unexportObject (registry, true);
			System.out.println("Server is stopped..");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
