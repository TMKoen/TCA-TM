import java.net.*;


public class TCAServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		try
		{
			ServerRMI serverRMI = new ServerRMI ();
			
			// Start the server.
			serverRMI.StartServer ();
			
			// Sleep for 5 minutes.
			Thread.sleep (5*60*1000);
			
			// Stopping the server.
			serverRMI.StopServer ();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		

		
	}

}
