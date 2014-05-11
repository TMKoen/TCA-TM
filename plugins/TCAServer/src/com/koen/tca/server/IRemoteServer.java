package com.koen.tca.server;

import java.io.ObjectOutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the RMI API for TCA
 * 
 */
public interface IRemoteServer extends Remote {

	/**
	 * Uploads a TestCase to the server. Only possible if the server is in the 'Wait' state
	 * @param testSet: the name of the testSet where the TestCase belongs.
	 * @param testCase: the testCase file to be upload
	 * @return: -1 if upload was not successful. 0 if upload was successful
	 * @throws RemoteException: if remote server is not accessible
	 */
	public int uploadTestCase(String testSet, ObjectOutputStream testCase) throws RemoteException;

	
	/**
	 * Place the server in the 'Detect' state and begins searching for candidate UE's
	 * This is only possible if the server is in the 'Idle' or 'Wait' state
	 * @throws RemoteException
	 */
	public void startDetect (ICallBackClient callBackClient) throws RemoteException;
	
	
	/**
	 * Stop searching for candidate UE's. If there are at least one UE available, then the server goes in the 'Wait' state
	 *  Otherwise the servers goes in the 'Idle' state
	 * @return: -1 if there where no UE's available. 0 if there are UE's
	 * @throws RemoteException: if remote server is not accessible
	 */
	public int StopDetect (ICallBackClient callBackClient) throws RemoteException;
	
	/**
	 *  Place the server in the 'Test' state and activate the test
	 * @param testSet
	 * @throws RemoteException
	 */
	public void startTestSet(ICallBackClient callBackClient, String testSet) throws RemoteException;

	/**
	 * Place the server in the 'Test' state and activate the test
	 * @param testCase
	 * @throws RemoteException
	 */
	public void StartTestCase (ICallBackClient callBackClient, String testCase) throws RemoteException;
	
	
	/**
	 * Stop the test (TestSet or TestCase) and place the server in the 'Wait' state
	 * @throws RemoteException
	 */
	public void stopTest(ICallBackClient callBackClient) throws RemoteException;

	
	public TestResults getTestResults () throws RemoteException;
	
	/**
	 * Gets all the UE's that the Server has a reference to.
	 * @return
	 * @throws RemoteException
	 */
	public String [] getUEList () throws RemoteException;
	
	/**
	 * Returns the status of the test Server
	 */
	public void getServerStatus () throws RemoteException;
	

} // interface RMIServerInterface
