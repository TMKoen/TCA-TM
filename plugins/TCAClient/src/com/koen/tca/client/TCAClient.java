
package com.koen.tca.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

public class TCAClient {

	public static void main(String[] args) {

		// the messages(string) that shows the possible parameters on the
		// command line
		final String mainParameters = "Possible parameters are:\n"
				+ "   starttestset\n"
				+ "   starttestcase <testcase name>\n" 
				+ "   stoptest\n"
				+ "   startdetect\n"
				+ "   stopdetect\n"
				+ "   uploadscript <scriptfile>\n"
				+ "   gettestresults\n"
				+ "   serverstatus\n"
				+ "   getuelist\n"
				+ "   ?\n"
				+ "   exit\n";

		final String welcomeText = "TCA version 1.0 \na Communication application to testcase Server.\n\n ";
		final String exitText = "Thank you and goodby!";
		
		BufferedReader commandlineText = new BufferedReader(new InputStreamReader(System.in));		
		
		
		// Show welcome display on the command line
		System.out.println(welcomeText);

		// Initiate a new ClinetRMI object witch communicate with the Server
		ClientRMI client;
		try {
			client = new ClientRMI();


		String command = "";
		String param = "";

		int index = 0;
		String tmp = "";
		while (!command.matches("exit")) {
			System.out.print("\nTCA>");

			try {
				tmp = commandlineText.readLine();
				tmp.toLowerCase();
			if ((index = tmp.indexOf(" ")) >= 0) {
					param = tmp.substring(index+1);	// gets the substring after the command string
					command = tmp.substring(0, index);
				} else {
					command = tmp.substring(0);
				}

				switch (command) {
				case "starttestset":
					System.out.println("Starting the test...\n");
					client.startTestSet();
					break;

				case "starttestcase":
					System.out.println("Starting the test...\n");
					client.startTestCase(param);
					break;
				
				
				case "stoptest":
					System.out.println("Stopping the test..\n");
					client.stopTest();
					System.out.println("done.");
					break;

				case "Uploadscript":
					System.out.println("Uploading testcase " + param + "\n");

					// Check if the second command line parameter (the script file
					// name) exists.
					if (args.length > 1) {
						client.uploadTestScript(param);
						System.out.println("done.");
					} else {
						System.out.println("No testcase filename!\n");
						System.out.println(mainParameters);
					}
					break;
					
				case "startdetect":
					System.out.println("Detecting for UE's is starting...\n");
					client.startDetect();
					break;
					
				case "stopdetect":
					System.out.println("Detecting for UE's is stopping...\n");
					client.stopDetect();
					break;
					
				case "gettestresults":
					System.out.println("Getting test results...\n");
					client.getTestResults();
					break;
					
				case "serverstatus":
					System.out.println("Status of the Server:\n");
					client.getServerStatus();
					break;
				
				case "getuelist":
					String [] uelist = null;
					System.out.println("Getting known user equipments...\n");
					uelist = client.getUEList();
					for (String ue : uelist) {
						System.out.println ("\t" + ue + "\n");
					}
					break;
					
				case "?":
					System.out.println(mainParameters);
					break;
				case "exit":
					System.out.println ("HOI:" + command + ":");
					break;
					
				default:
					System.out.println("No valid command\n");

				break;
				}
				
			} catch (Exception e) {
				
			}
			
		}

		// Shows the last exit text.
		System.out.println (exitText);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}  // main

	
} // class TCAClient