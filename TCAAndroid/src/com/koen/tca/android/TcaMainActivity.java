package com.koen.tca.android;

import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.android.state.AndroidEvents;
import com.koen.tca.android.state.AndroidStateMachine;
import com.koen.tca.android.state.AndroidStateTest;
import com.koen.tca.android.state.IAndroidState;
import com.koen.tca.android.util.SystemUiHider;







import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/* 
 * @Author Koen Nijmeijer
 * This TCAAndroid app. don't use Dependency injection like Guice or GoboGuice.
 * Google discouraged it for use on Android phones because of slow speed and more memory use.
 * @see http://developer.android.com/training/articles/memory.html#DependencyInjection
 * 
 * " Avoid dependency injection frameworks
 * Using a dependency injection framework such as Guice or RoboGuice may be attractive
 *  because they can simplify the code you write and provide an adaptive environment 
 *  that's useful for testing and other configuration changes. 
 *  However, these frameworks tend to perform a lot of process initialization by 
 *  scanning your code for annotations, which can require significant amounts of your 
 *  code to be mapped into RAM even though you don't need it. These mapped pages are 
 *  allocated into clean memory so Android can drop them, but that won't happen until 
 *  the pages have been left in memory for a long period of time."
 * 
 */


/**
 * Main class for the TCA Android App.
 * @version
 * @author Koen Nijmeijer
 *
 */
public class TcaMainActivity extends Activity {

	// A reference to the singleton object which hold the device identification 
	// information like IMEI and telephone number.
	// It also holds the IP address of the Server and the port number to communicate to the Server.
	private DeviceIdentifier deviceInfo;
	
	// Holds and manage the state of the Android device: Idle, Expose, Ready or Test.
	private AndroidStateMachine androidState;
	
	// The port number for the socket connection to the Server.
	private final int PORTNUMBER = 1099;
	
	// handler to catch messages from other threads (to the main thread). These messages 
	// has to do with changing the state of the Android (Idle, Expose, Ready or Test).
	private Handler mainHandler;
	
	// The handler to the UI button that starts exposing the device to the Server.
	private Button button_StartExpose;
	
	// The handler to the UI button that stops a running test by hand.
	private Button button_StopTest;
	
	// The handler to the UI EditText field where the engineer can write the Server IP address.
	private EditText editText_IpAddress;
	
	// The handler to the UI EditText field where the user can write te telephone number of the Android device.
	private EditText editText_phoneNumber;
	
	// The handler to the UI state TextView.
	private TextView textView_State;
	
	// The handler to the UI status TextView.
	private TextView textView_Status;
	
	// The handler to the UI EMEI TextView.
	private TextView textView_Imei;
	
	// the handler to the UI Telephone number TextView
	private TextView textView_Number;

	// Creates an object of ActionRunnner. It is a singleton, so there can be only one object of this class.
	// SuppressWarnings suppress the compiler to say that this variable is never used.
	@SuppressWarnings("unused")
	private ActionRunner actionRunner = ActionRunner.SINGLETON();
	
	/**
	 * Initialize and starts the activity (user interface).
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param savedInstanceState a Bundle object 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Initialize the window UI like buttons, textview, etc.		
		setContentView(R.layout.activity_tca_main);
		
		// find the UI objects (buttons. text fields, etc).
		editText_IpAddress = (EditText) findViewById (R.id.EditText_ipaddr);
		editText_phoneNumber = (EditText) findViewById(R.id.editText_telephone_nr);
		button_StartExpose = (Button) findViewById(R.id.button_find_server);
		button_StopTest = (Button) findViewById (R.id.button_stop_testing);
		textView_State = (TextView) findViewById(R.id.textView_State);
		textView_Status = (TextView) findViewById (R.id.textView_Status);
		textView_Imei = (TextView) findViewById(R.id.textView_Imei);
		textView_Number = (TextView) findViewById(R.id.textView_Number);
		

		// gets the DeviceIdentifier object (singleton) and initialize it with the IMEI 
		// and telephone number of this Android device.
		deviceInfo = DeviceIdentifier.SINGLETON();
		deviceInfo.initDeviceIdentification(this);
		if (deviceInfo.getTelephoneNumber() == null || "".equals(deviceInfo.getTelephoneNumber())) {
			// The telephone number was not stored on the SIM card, so it must be fill in by hand.
			editText_phoneNumber.setVisibility(View.VISIBLE);
		}

		// Create a message queue for catching messages from other threads.
		// These messages commands to change the present state (Idle, Expose, Ready or Test).
		mainHandler = new Handler () {

			// The handle message method to catch the messages and do something with it.
			@Override
			public void handleMessage(Message msg) {
				// Check if the message is and event message. Otherwise, do nothing with it.
				if (msg.obj instanceof AndroidEvents) {
					AndroidEvents event = (AndroidEvents) msg.obj;	
				
					// Change the present state, dependent of the parameter:event.
					// It the wrong event is send, the present state is'n changed.
					// So the thread (or the Server) that sends this message,
					// must be aware of the present state.
					androidState.changeState(event, mainHandler);
				
					// Displays the Android device state on the UI.
					showState ();

					// change the UI components to enable or disable, depends of the present state.
					setUIComponentsEnabled(androidState.getState().toString());
				} else {
					// The message must be an event message, so this block would never been used!
				}
			} 
		};		

		// Initialize the state machine. The Android goes to the Idle state by default.
		androidState = new AndroidStateMachine ();
		

		// Displays the Android device state on the UI.
		showState ();
		
		// display the Android device IMEI and telephone number on the UI.
		showImei ();
		showNumber ();

		// change the UI components to enable or disable, depends of the present Idle state (the default by initializing).
		setUIComponentsEnabled(androidState.getState().toString());
		
		 // Initialize the onClick method that is activated when a user (engineer) push on
		// the UI button: Register device
		button_StartExpose.setOnClickListener(new Button.OnClickListener () {
			@Override
			public void onClick(View arg0) {
				
				// Gets the IP address. If it is not valid, "0" is returned.
				String ipaddr = getValidIpAddress();
				
				// Check if the IP address is valid.
				if (!ipaddr.equals("0")) {
					
					// Clears the status on the UI.
					showStatus("");					
					// Add the Server IP address and the port number to the singleton deviceInfo object.
					deviceInfo.initServerCommunication(ipaddr, PORTNUMBER);

					// Check if the Android device telephone number is found automatically.
					if (deviceInfo.getTelephoneNumber()== null || "".equals(deviceInfo.getTelephoneNumber ())) {
						// Get the telephoneNumber filled in by the user and place it in deviceInfo.
						// If the user don't fill in a (valid) number, then it is null.
						deviceInfo.setTelephoneNumber(getTelephoneNumber());

						// Display the telephone number on the UI.
						showNumber ();
					}
			
					// check it a second time, because deviceInfo has already a valid number from
					// the previous if statement.
					if (deviceInfo.getTelephoneNumber() != null && !"".equals(getTelephoneNumber())) {
						// go to the Expose state
						androidState.changeState(AndroidEvents.START_EXPOSE, mainHandler);
						
						// Change the UI components to enable or disable, depends of the present state.
						setUIComponentsEnabled(androidState.getState().toString());	
					} else {
						// Shows a message on the UI that the user must fill in a telephone number.
						showStatus ("Error: no (valid) telephone number!");
					}
								
				} else {
					// Shows a message on the UI that the user must fill in a Server IP address.
					showStatus("Error: no valid Server IP address!");
				}			
				
				// Displays the Android device state on the UI.
				showState ();
			}
			
		});
		
		 // onClick is executed when the user push on the 'Stop testing' button
		button_StopTest.setOnClickListener(new Button.OnClickListener () {
			@Override
			public void onClick(View arg0) {
				if (androidState.getState() instanceof AndroidStateTest) {

					// Clears the status on the UI.
					showStatus ("");
					androidState.changeState(AndroidEvents.STOP_TEST, mainHandler);

					// change the UI components to enable or disable, depends of the present state.
					setUIComponentsEnabled (androidState.getState().toString());

				} else {
					// The test can't be stopped because the Android isn't in the Test state.
					// This block should never be activated because the UI button: 'Stop testing'
					// is disabled and hidden (see method: setUIComponentsEnabled(..))
					
					// Shows a message on the UI that the device isn't in the test state.
					showStatus ("Error: the device is not testing on this moment!");
				}	
				
				// Displays the Android device state on the UI.
				showState ();
				
				// Enable/Disable the UI components
				setUIComponentsEnabled(androidState.getState().toString());
			}
		});
			
	}	// onCreate
	
	/**
	 * shows the state of the Android device on the UI.
	 * @version
	 * @author Koen Nijmeijer 
	 */
	private void showState () {
		textView_State.setText ("State: " + androidState.getState().toString());
	}
	
	/**
	 * Shows the status on the UI.
	 * @version
	 * @author Koen Nijmeijer
	 * @param status the status string to be showed
	 */
	private void showStatus (String status) {
		textView_Status.setText ("Status: " + status);
	}
	
	/**
	 * Shows the IMEI number on the UI.
	 * @version
	 * @author Koen Nijmeijer
	 */
	private void showImei () {
		if (deviceInfo.getImeiNumber() != null || !"".equals(deviceInfo.getImeiNumber())) {
			textView_Imei.setText("IMEI: " + deviceInfo.getImeiNumber());			
		} else {
			textView_Imei.setText("This device has no Imei Number!");
		}

	}
	
	/**
	 * Shows the telephone number on the UI.
	 * @version
	 * @author Koen Nijmeijer
	 */
	private void showNumber () {
		if (deviceInfo.getTelephoneNumber() != null && !"".equals(deviceInfo.getTelephoneNumber())) {
			textView_Number.setText ("Number: " + deviceInfo.getTelephoneNumber());
		} else {
			textView_Number.setText("No telephone number found!");
		}
	}
	
	private String getTelephoneNumber () {
		String nr = editText_phoneNumber.getText ().toString();
		if (nr == null || "".equals(nr)) {
			nr = "";
		}
		return nr;
	}
	
	/**
	 * get the IP address from the UI and check if its valid.
	 * <p>
	 * The IP address must between 0.0.0.0 and 255.255.255.255 and <br>
	 * one of the private address: <br>
	 * 		- 127.0.0.0 (loopback address) <br>
	 * 		- 10.0.0.0 and 10.255.255.255 <br>
	 * 		- 172.16.0.0 and 172.31.255.255 <br>
	 * 		- 192.168.0.0 and 192.168.255.255 <br> <br>
	 * PS: it don't check if the address is a broadcast address! 
	 * @version
	 * @author Koen Nijmeijer
	 * @return a valid IP address or "0" if not
	 */
	private String getValidIpAddress () {

		// Initialize the return value.
		String validIpAddress = "0";
		
		// Get the IP address from User input.
		String ipAddr = editText_IpAddress.getText().toString();
		
		// IP address must between: 0.0.0.0 - 255.255.255.255
		// "\\d" means: any digit from 0-9
		// "\\." means: '.'
		// ? 	 means: not necessary, but allowed. 
		//[01] and [0-4] and [0-5] means: only one of this digits are allowed.
		final String IPPATTERN = 
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + 
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		
		if (ipAddr.matches(IPPATTERN)) {
			// IP address matches the pattern.
			
			// Splits the IP address in four parts, each part is one octal of the IP address.
			String octals[] = ipAddr.split("\\.");

			if (octals.length == 4) {		
				// it must be, because we checked it earlier with ipAddr.matches(IPPATTERN)
		
				// check if IP address is:
				//			- 10.x.x.x or 010.x.x.x
				//			- 172.16.x.x to 172.31.x.x
				//			- 192.168.x.x
				//			- 127.0.0.1
				validIpAddress = (
						(octals[0].equals("010") || octals[0].equals("10")) ||
						(octals[0].equals("172") && 
								(Integer.valueOf(octals[1]) >=16 && 
								 Integer.valueOf(octals[1]) <=31)) ||
						(octals[0].equals("192") && octals[1].equals("168")) ||
						(octals[0].equals("127") && 
								octals[1].equals("0") && 
								octals[2].equals("0") && 
								octals[3].equals("1"))
				)?ipAddr :"0";
			}
		}
		
		// return "0" if the IP Address was not valid. remove the first "0" in 010.x.x.x.
		return (validIpAddress.charAt(0) == '0' && validIpAddress.length()>1)? validIpAddress.substring(1) :validIpAddress;
	}

	/**
	 * Enable or disable the UI Components (like buttons and text fields), dependent of
	 * the present state (Idle, Expose, Ready or Test).
	 * This method is only accessible by the class itself (private)
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param state the string value of the present state. 
	 */
	private void setUIComponentsEnabled (String state) {

		// Enable/Disable the UI components.
		button_StartExpose.setEnabled((state=="Android Idle" || state=="Android Ready")?true: false);
		button_StopTest.setEnabled((state=="Android Test")?true: false);
		editText_IpAddress.setEnabled((state=="Android Idle" || state=="Android Ready")?true: false);
		editText_phoneNumber.setEnabled((state=="Android Idle")?true: false);

		// Hide/Shows the UI components.
		button_StartExpose.setVisibility((state=="Android Idle" || state=="Android Ready")?View.VISIBLE: View.GONE);
		button_StopTest.setVisibility((state=="Android Test")?View.VISIBLE: View.GONE);
		editText_IpAddress.setVisibility((state=="Android Idle" || state=="Android Ready")?View.VISIBLE: View.GONE);
		editText_phoneNumber.setVisibility((state=="Android Idle")?View.VISIBLE: View.GONE);
		
	} // setUIComponentsEnabled
}
