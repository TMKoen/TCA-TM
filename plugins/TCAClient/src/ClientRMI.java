import java.rmi.*;
import java.rmi.registry.*;
import java.net.*;


public class ClientRMI {

	// Data members
	
	private final String serverAddress = "192.168.56.1";		// The server- IP address
	private final int serverPort = 1099;					// The Port for the Client-Server communication. By default RMI use 1099
	private final String serverRmi = "ServerRMI";
	
	private RmiServerInterface serverObj;
	private Registry registry;
	
	// Constructors
	public ClientRMI ()
	{

		try {
			// Get the Registry from the Server
			registry = LocateRegistry.getRegistry(serverAddress, serverPort);
			
			// Finds the remove Server object
			serverObj = (RmiServerInterface)registry.lookup(serverRmi);

		}
		catch (RemoteException e)
		{
			System.out.println("Server is not online2!");
		}
		catch (NotBoundException e)
		{
			System.out.println("the Server is not Registerd (bounded) int the registry!\n try to start te server first.\n");
		}
	}
	
	// Methods
	public void StartTest ()
	{
		try
		{

			serverObj.StartTest ();
		}
		catch (RemoteException e)
		{
			System.out.println ("Server is not online3!");
		}
		
	}
	
	public void StopTest ()
	{
		try
		{
			serverObj.StopTest ();
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
	
	public void AddTestCase (String testCaseName)
	{
		try
		{
			serverObj.AddTestCase (testCaseName);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
	

}
