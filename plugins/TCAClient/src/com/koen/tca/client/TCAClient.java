package com.koen.tca.client;
public class TCAClient {

	public static void main(String[] args) {

		// the messages(string) that shows the possible parameters on the
		// command line
		final String mainParameters = "Possible parameters are:\n   Start\n   Stop\n   Addscript <scriptfile>\n";

		final String welcomeText = "TCA version 1.0 \na Communication application to testcase Server.\n\n ";

		final int FIRST_ARG = 0; // The index for the first argument that is
									// given by the command line
		final int SECOND_ARG = 1; // the index for the second argument that is
									// given by the command line

		// Show welcome display on the command line
		System.out.println(welcomeText);

		// Initiate a new ClinetRMI object witch communicate with the Server
		ClientRMI client = new ClientRMI();

		// Check for parameters on the command line
		if (args.length > 0) {
			// Switch to the right method dependent on the first argument from
			// the command line

			switch (args[FIRST_ARG].toLowerCase()) {
			case "start":
				System.out.println("Starting the test...\n");
				client.startTest();
				System.out.println("done.");
				break;

			case "stop":
				System.out.println("Stopping the test..\n");
				client.stopTest();
				System.out.println("done.");
				break;

			case "addscript":
				System.out.println("Adding a testcase...\n");

				// Check if the second command line parameter (the script file
				// name) exists.
				if (args.length > 1) {
					client.addTestCase(args[SECOND_ARG]);
					System.out.println("done.");
				} else {
					System.out
							.println("No testcase filename!\nAdding a testcase is stopped!\n");
					System.out.println(mainParameters);
				}
				break;
			default:
				System.out.println("No valid paramers!\n");
				System.out.println(mainParameters);
				break;
			}

		} else {
			// No parameters on the command line. Show the possible parameters
			// on the command line
			System.out.println("No parameters present!");
			System.out.println(mainParameters);
		}

	}

}