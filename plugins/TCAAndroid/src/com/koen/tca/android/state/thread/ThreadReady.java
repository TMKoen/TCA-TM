package com.koen.tca.android.state.thread;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.koen.tca.android.ActionRunner;
import com.koen.tca.android.action.ActionObjects;
import com.koen.tca.android.action.ITestAction;
import com.koen.tca.common.message.AcknowledgeMessage;
import com.koen.tca.common.message.AndroidEvents;
import com.koen.tca.common.message.ChangeStateMessage;
import com.koen.tca.common.message.ActionMessage;
import com.koen.tca.common.message.IMessage;
import com.koen.tca.common.message.RemoteAction;
import com.koen.tca.common.message.RemoteMessageTransmitter;
import com.koen.tca.common.message.RemoteResults;
import com.koen.tca.common.message.RemoteUe;
import com.koen.tca.common.message.RequestResultsMessage;
import com.koen.tca.common.message.ResultsMessage;

/**
 * The thread that handles the Ready state
 * @version 1.0
 * @author Koen Nijmeijer
 *
 */
public class ThreadReady implements IThreadState {

	// The port number where the server can reach the Android.
	private int androidPortNumber = 8899;
	
	/**
	 *  Default class for testing the Actions. There is no connection with the server.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 *
	 */
	public class TestThreadReady extends ThreadReady {
		
		public TestThreadReady () {
			super ();
		}

		@Override
		public void startThread (Handler mainActivityHandler) {
			if (readyThread == null) {
				mainHandler = mainActivityHandler;
				readyThread = new Thread(this, threadName);
				setStopThread(false);
				readyThread.start();
			}
		}
		
		@Override
		public void stopThread () {
			setStopThread(true);
		}
		
		@Override
		public void run () {

			IMessage remoteMsg = null;
			AndroidEvents event;

			RemoteUe ue1 = new RemoteUe ();
			ue1.setImei("354720050558902");
			ue1.setMsisdn("0648603223");
			ue1.setName("ue1");
			
			RemoteUe ue2 = new RemoteUe ();
			ue2.setImei("");
			ue2.setMsisdn("0648603226");
			ue2.setName("u2");

			RemoteUe ue3 = new RemoteUe ();
			ue3.setImei("353687065694298");
			ue3.setMsisdn("0653740326");
			ue3.setName("ue3");

			
			RemoteAction remoteActionCall = new RemoteAction ("CALL");
			RemoteAction remoteActionAnswer = new RemoteAction ("ANSWER");
			RemoteAction remoteActionSMS = new RemoteAction ("SMS");
			RemoteAction remoteActionData = new RemoteAction ("DATA");
			RemoteAction remoteActionUssd = new RemoteAction ("USSD"); 

			// Set parameters for call action
			remoteActionCall.getMap().put("from", "ue1");
			remoteActionCall.getMap().put("to", "ue3");
			remoteActionCall.getMap().put("callingTime", "5");
			remoteActionCall.getMap().put("responseTime", "10");
			remoteActionCall.getMap().put("offHookResponse", "HangUp"); // can be: Listening or HangUp
			remoteActionCall.getUeMap().put(ue1.getName(), ue1);
			remoteActionCall.getUeMap().put(ue3.getName(), ue3);
			
			// Set parameters for Answer action
			remoteActionAnswer.getMap().put("from", "ue1");
			remoteActionAnswer.getMap().put("listeningTime", "10");
			remoteActionAnswer.getMap().put("answerTime", "10");
			remoteActionAnswer.getMap().put("response","HangUp");	// can be: Listening, OffHook or HangUp
			remoteActionAnswer.getUeMap().put(ue1.getName(), ue1);
	
			// Set parameters for SMS action
			remoteActionSMS.getMap().put("from", "ue1");
			remoteActionSMS.getMap().put("to", "ue3");
			remoteActionSMS.getMap().put("message", "Dit is een testSMS van 0653740326 naar 0648603223.");
			remoteActionSMS.getUeMap().put(ue1.getName(), ue1);
			remoteActionSMS.getUeMap().put(ue3.getName(), ue3);
			
			// Set parameters for Data action
			remoteActionData.getMap().put("from", "ue2");
			remoteActionData.getMap().put("url","http://www.verkeersschoolkoen.nl/verkeersschoolkoen.php");
			remoteActionData.getMap().put("to", "ue3");
			remoteActionData.getMap().put("data", "Data");
			remoteActionData.getUeMap().put(ue1.getName(), ue1);
			remoteActionData.getUeMap().put(ue3.getName(), ue3);
			
			// Set parameters for USSD
//			remoteActionUssd.getMap().put("from", "ue1");
//			remoteActionUssd.getMap().put("cfCode", "CFU");	// can be: CFU, CFB, CFNA, CFNR, ACD
//			remoteActionUssd.getMap().put("destination", "ue3");	// only by cfCode
//			remoteActionUssd.getMap().put("serviceDelay", "10");	// only by cfCode
//			remoteActionUssd.getMap().put("barringCode", "AllOutgoingCalls");		// Can be: AllOutgoingCalls, AllIncomingCalls, AllOutgoingCallsExHome, AllIncommingCallsRoaming
//			remoteActionUssd.getMap().put("code", "10");	// Only by barringCode. code can be: 
			remoteActionUssd.getMap().put("liCode", "CLIR");	// can be: CLIP, CLIR, COLP or COLR
			remoteActionUssd.getMap().put("ussdregistration", "Activate");	// can by Activate or Deactivate 
			remoteActionUssd.getUeMap().put(ue1.getName(), ue1);
			
			while (stopThread != true) {


				// simulate a received RemoteMessage.
				remoteMsg = new ActionMessage (remoteActionUssd);

				if (remoteMsg != null && remoteMsg instanceof ActionMessage) {
					RemoteAction intentAction = ((ActionMessage) remoteMsg)
							.getAction();
					
					// Get the parameters
					Map <String, String> map = intentAction.getMap();
					Map <String, RemoteUe> ueMap = intentAction.getUeMap();

					boolean isValidActionName = false;
					
					// Checks if the Action name is valid.
					for (ActionObjects a: ActionObjects.values()) {
						// Checks if the name of the Action is a valid name that exists in the ActionObjects enum.
						if (a.name().equals("Action" +intentAction.getActionName())) {
							isValidActionName = true;
							break;
						}
					}

					// gets the singleton object to store the action.
					ActionRunner actionRunner = ActionRunner.SINGLETON();
					boolean isValidAction = true;

					if (isValidActionName) {
						// Action name is valid, so create a new object of that name and put it in actionRunner.
						 try {

							 // creates dynamically a ITestAction object depends on the name of the action.
							// The classname must be a fully qualified name so inclusive the package name.
							 actionRunner.setAction((ITestAction) Class.forName("com.koen.tca.android.action.Action"+intentAction.getActionName()).newInstance());
				
							 // Set the parameters to the action class
							 actionRunner.getAction().setParameters(map);
							 actionRunner.getAction().setUeParameters(ueMap);
							 
						 } catch (InstantiationException e1) {
							 // The action class has no default constructor like ActionName ().
							 Log.e("Instantioation", "The action class has no default constructor without parameters");
							 isValidAction = false;
						 } catch (IllegalAccessException e1) {
							 // The action class is private or protected.
							 Log.e ("IlligalAccess","The action class is private or protected");
							 isValidAction = false;
						 } catch (ClassNotFoundException e1) {
							 // action class is not present. It should be!
							 Log.e("No class fount","The action class isn't found");
							 isValidAction = false;
						 }
					} else {
						// no valid Action name. So Android can't do anything with this Action message.
						isValidAction = false;
					}
					
					if (!isValidAction) {
						// send acknowledge message back to the server. The server don'n send an ChangeState messages anymore.
						
						remoteMsg = null;
					} else {
					
						// Receive a ChangeState message only if the Remote action is OK.
						remoteMsg = new ChangeStateMessage(AndroidEvents.START_TEST);						 
					}
				} // if isValidActionName

				
				if (remoteMsg != null && remoteMsg instanceof RequestResultsMessage) {
					// The server asks for the results.
					
					// TODO: Fills this with the results.
					RemoteResults remoteResults = new RemoteResults ("");
					
					// Send a ResultsMessage to the server.


					// Reads an Acknowledge message from the server.
					IMessage remoteMsgAck = new AcknowledgeMessage();
					
					if (remoteMsgAck instanceof AcknowledgeMessage) {
						// the results are by the server know, so clean the old results.
						//TODO: remoteResults.clear ()
					}
					// clears the Remote message.
					remoteMsg = null;				
				}

				if (remoteMsg != null && remoteMsg instanceof ChangeStateMessage) {
					// gets the event from the received message
					event = ((ChangeStateMessage) remoteMsg).getEvent();

					// go out the thread
					setStopThread(true);
					
					// sends the event to the main thread (UI thread).
					Message msg = mainHandler.obtainMessage();
					msg.obj = event;
					mainHandler.sendMessage(msg);
				} 
			} // while
		}
	}

	// End of test run()
	/******************************* */
	
	private Thread readyThread;
	private Handler mainHandler;
	private boolean stopThread;
	private ServerSocket androidServerSocket;
	private final int timeout = 1000 * 60;
	private final int readTimeout = 1000 * 30;
	
	final String threadName = "Ready";

	public ThreadReady() {
		readyThread = null;
		mainHandler = null;
		setStopThread(true);
	}

	public synchronized void startThread(Handler mainActivityHandler) {
		if (readyThread == null) {
			mainHandler = mainActivityHandler;
			readyThread = new Thread(this, threadName);
			setStopThread(false);
			readyThread.start();
		}
	}

	public synchronized void stopThread() {
		// Stops the thread
		setStopThread(true);
	}

	public void run() {

		IMessage remoteMsg = null;
		AndroidEvents event;

		// Create an object which can handle messages (sending/receiving) to and
		// from the Server.
		RemoteMessageTransmitter messageTransmitter = new RemoteMessageTransmitter();

			try {
				androidServerSocket = new ServerSocket(
						androidPortNumber);
				// Set the timeout how long the server socket accept () method
				// must hold.
				androidServerSocket.setSoTimeout(timeout);

			} catch (SocketException e) {
				// setSoTimeOut can't set a timeout.
				// No other signal is made for the user!!!!!!!
				Log.e("setSoTimeOut", "The Ready state can't set a timeout for the serversocket");

				// Stop the thread.
				setStopThread(true);
				
				// Change the state to Idle.
				event = AndroidEvents.IDLE;
				Message msg = mainHandler.obtainMessage();
				msg.obj = event;
				mainHandler.sendMessage(msg);
				
			} catch (IOException e) {
				// androidServerSocket can't make a new ServerSocket
				// So go to the Idle mode.
				Log.e("androidServerSocket", "The Ready state can't make a new ServerSocket");

				// Stop the thread.
				setStopThread(true);
				
				// change the state to Idle.
				event = AndroidEvents.IDLE;
				Message msg = mainHandler.obtainMessage();
				msg.obj = event;
				mainHandler.sendMessage(msg);

			}
				int noConnectioncounts = 0;
				
			while (stopThread != true) {

				Socket clientSocket = null;
				try {
					// waits for a command from the Server or a timeout occurs.
					clientSocket = androidServerSocket.accept();
				} catch (InterruptedIOException e) {
					// a timeout occurs while waiting on a accept connection.

					// skip the rest, go back to the while loop.
					continue;
				
				} catch (IOException e) {
					// clientSocket can't make an accept connection
					Log.e("clientSocket","clientSocket can't make an accept connection for the " + noConnectioncounts+1 + "e time");
					if (clientSocket != null) {
						try {
							clientSocket.close ();
						} catch (IOException e1) {
							Log.e ("clientSocket","clientSocket can't be closed");
						}
					}
					noConnectioncounts++;
					if (noConnectioncounts == 20) {
					// to much no client connections possible.. Stop and go the the Idle state.	

						// Stop the thread.
						setStopThread(true);
						
						event = AndroidEvents.IDLE;
						Message msg = mainHandler.obtainMessage();
						msg.obj = event;
						mainHandler.sendMessage(msg);
					}
					// skip the rest, go back to the while loop.
					continue;
				}

					// Reset counting of unsuccessful connections, because on this point, there IS a connection.
					// After this present connection, the count must begin again for a new connection.
					noConnectioncounts = 0;
				

				try {
					// Sets the output stream.
					messageTransmitter.setOutputStream(clientSocket.getOutputStream());
					
					// Sets the input stream.
					messageTransmitter.setInputStream(clientSocket.getInputStream());

				} catch (IOException e1) {
					// Input stream or output stream can't make a connection
					Log.e("output stream or input stread", "The output- or input stream can't be made");

					// If its possible, close the input and output streams
					messageTransmitter.closeOutputStream();
					messageTransmitter.closeInputStream();
					
					// Close the client socket.
					if (clientSocket != null) {
						try {
							clientSocket.close();
						} catch (IOException e2) {
							Log.e("clientSocket", "clientSocket can't be closed");
						}
					}
					// skip the rest, go back to the while loop.
					continue;
				}
				
				// Reads the first message from the server. If there is some problem, the message = null.
				remoteMsg = messageTransmitter.receiveMessage(clientSocket, readTimeout);


				if (remoteMsg != null && remoteMsg instanceof ActionMessage) {
					RemoteAction intentAction = ((ActionMessage) remoteMsg)
							.getAction();
					
					// Get the parameters
					Map <String, String> map = intentAction.getMap();
					Map <String, RemoteUe> ueMap = intentAction.getUeMap();

					boolean isValidActionName = false;
					
					// Checks if the Action name is valid.
					for (ActionObjects a: ActionObjects.values()) {
						// Checks if the name of the Action is a valid name that exists in the ActionObjects enum.
						if (a.name().toLowerCase().equals("action" +intentAction.getActionName().toLowerCase())) {
							isValidActionName = true;
							break;
						}
					}

					// gets the singleton object to store the action.
					ActionRunner actionRunner = ActionRunner.SINGLETON();
					boolean isValidAction = true;

					if (isValidActionName) {
						// Action name is valid, so create a new object of that name and put it in actionRunner.
						 try {

							 // creates dynamically a ITestAction object depends on the name of the action.
							// The classname must be a fully qualified name so inclusive the package name.
							 actionRunner.setAction((ITestAction) Class.forName("com.koen.tca.android.action.Action"+intentAction.getActionName()).newInstance());
				
							 // Set the parameters to the action class
							 actionRunner.getAction().setParameters(map);
							 actionRunner.getAction().setUeParameters(ueMap);
							 
						 } catch (InstantiationException e1) {
							 // The action class has no default constructor like ActionName ().
							 Log.e("Instantioation", "The action class has no default constructor without parameters");
							 isValidAction = false;
						 } catch (IllegalAccessException e1) {
							 // The action class is private or protected.
							 Log.e ("IlligalAccess","The action class is private or protected");
							 isValidAction = false;
						 } catch (ClassNotFoundException e1) {
							 // action class is not present. It should be!
							 Log.e("No class fount","The action class isn't found");
							 isValidAction = false;
						 }
					} else {
						// no valid Action name. So Android can't do anything with this Action message.
						isValidAction = false;
					}
					
					if (!isValidAction) {
						// send acknowledge message back to the server. The server don'n send an ChangeState messages anymore.
						messageTransmitter.sendMessage(new AcknowledgeMessage("No valid Remote Action!"));
						remoteMsg = null;
					} else {
						messageTransmitter.sendMessage(new AcknowledgeMessage("Remote action is ok!"));
						
						
						// Receive a ChangeState message only if the Remote action is OK.
						remoteMsg = messageTransmitter.receiveMessage(clientSocket, readTimeout);						 
					}
				} // if isValidActionName

				
				if (remoteMsg != null && remoteMsg instanceof RequestResultsMessage) {
					// The server asks for the results.
					
					
					ActionRunner actionRunner = ActionRunner.SINGLETON();
					
					// Get the results from the last test.
					RemoteResults remoteResults = actionRunner.getResults();	
					
					// Send a ResultsMessage to the server.
					messageTransmitter.sendMessage(new ResultsMessage (remoteResults));

					// Reads an Acknowledge message from the server.
					IMessage remoteMsgAck = messageTransmitter.receiveMessage(clientSocket, readTimeout);
					
					if (remoteMsgAck instanceof AcknowledgeMessage) {
						// the results are by the server know, so clean the old results.
						actionRunner.clean();
					}
					// clears the Remote message.
					remoteMsg = null;
					
				}

				if (remoteMsg != null && remoteMsg instanceof ChangeStateMessage) {
					// gets the event from the received message
					event = ((ChangeStateMessage) remoteMsg).getEvent();

					// go out the thread
					setStopThread(true);
					
					// sends the event to the main thread (UI thread).
					Message msg = mainHandler.obtainMessage();
					msg.obj = event;
					mainHandler.sendMessage(msg);
				} 

				messageTransmitter.closeOutputStream();
				messageTransmitter.closeInputStream();
				if (clientSocket != null) {
					try {
						clientSocket.close();
					} catch (IOException e) {
						Log.e ("clientSocket","the clientsocket can't be closed");

					}
				}
			} // while

			if (androidServerSocket != null) {
				try {
					androidServerSocket.close();
				} catch (IOException e) {
					Log.e("androidServerSocket", "androidServerSocket can't be closed");
				}
			}


	} // method run ()

	private synchronized void setStopThread(boolean stopThread) {
		this.stopThread = stopThread;
	}
} // class ThreadReady

