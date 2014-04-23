import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServerInterface extends Remote {

	
	public void StartTest () throws RemoteException;
	public void StopTest () throws RemoteException;
	public void AddTestCase (String testCase) throws RemoteException;
}
