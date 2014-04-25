package com.koen.tca.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the RMI API for TCA
 * 
 */
public interface RmiServerInterface extends Remote {

	public void startTest() throws RemoteException;

	public void stopTest() throws RemoteException;

	public void addTestCase(String testCase) throws RemoteException;

	public void startServer();

	public void stopServer();

}
