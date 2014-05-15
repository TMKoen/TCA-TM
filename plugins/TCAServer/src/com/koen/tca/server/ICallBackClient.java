package com.koen.tca.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


// These methods are responses of the Remote Method Invocations (RMI) from the Client to the Server.
// The Server calls the client back if the test or the detection of UE's are failed or finished.
// This interface don't has to be registered in a naming service because after calling the method,
// any reference to it is garbage collected.
public interface ICallBackClient extends Remote {
	public void testIsDone () throws RemoteException;
	public void testFailed (String reason) throws RemoteException;
	public void detectFailed (String reason) throws RemoteException;
	public void detectIsDone (String [] ue) throws RemoteException;
}
