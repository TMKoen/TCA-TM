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
	
	// The handler to the UI status TextView.
	private TextView textView_status;

	// Creates an object of ActionRunnner. It is a singleton, so there can be only one object of this class.
	private ActionRunner action = ActionRunner.SINGLETON();
	
	/**
	 * The main method that starts and initialize the activity (user interface).
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// gets the DeviceIdentifier object (singleton) and initialize it with the IMEI 
		// and telephone number of this Android device.
		deviceInfo = DeviceIdentifier.SINGLETON();
		deviceInfo.initDeviceIdentification(this);

		// Initialize the window UI like buttons, textview, etc.		
		setContentView(R.layout.activity_tca_main);
	
		// Create a message queue for catching messages from other threads.
		// These messages commands to change the present state (Idle, Expose, Ready or Test).
		mainHandler = new Handler () {
			// The handle message method to catch the messages and do something with it.
		@Override
		public void handleMessage(Message msg) {
				AndroidEvents event = (AndroidEvents) msg.obj;
				
				// Change the present state, dependent of the parameter:event.
				// It the wrong event is send, the present state is'n changed.
				// So the thread (or the Server) that sends this message,
				// must be aware if the present state.
				androidState.changeState(event, mainHandler);
				
				// Displays the status on the UI.
				textView_status.setText("State: "+ androidState.getState().toString());

				// change the UI components to enable or disable, depends of the present state.
				setUIComponentsEnabled(androidState.getState().toString());
			} 
		};		

		// Initialize the state machine. The Android goes to the Idle state by default.
		androidState = new AndroidStateMachine ();
		
		// Initialize the UI objects (buttons. text fields, etc).
		editText_IpAddress = (EditText) findViewById (R.id.EditText_ipaddr);
		button_StartExpose = (Button) findViewById(R.id.button_find_server);
		button_StopTest = (Button) findViewById (R.id.button_stop_testing);
		textView_status = (TextView) findViewById (R.id.textView_Status);

		textView_status.setText("State: " + androidState.getState().toString());

		// change the UI components to enable or disable, depends of the present Idle state (the default by initializing).
		setUIComponentsEnabled(androidState.getState().toString());
		
		 // Initialize the onClick method that is activated when a engineer push on
		// the UI button: Register device
		button_StartExpose.setOnClickListener(new Button.OnClickListener () {
			@Override
			public void onClick(View arg0) {
				String ipaddr = editText_IpAddress.getText().toString();
				// IP address must at least 4 digits and 3 dots (like 0.0.0.0)
				if (ipaddr.length() >=7) {
					
					// Add the Server IP address and the port number to the singleton deviceInfo object.
					deviceInfo.initServerCommunication(ipaddr, PORTNUMBER);

					// go to the Expose state
					androidState.changeState(AndroidEvents.START_EXPOSE, mainHandler);
					
					// change the UI components to enable of disable, depends of the present state.
					setUIComponentsEnabled(androidState.getState().toString());
					
					// Sets the UI status text field
					textView_status.setText("State: " + androidState.getState().toString());
				} else {
					// shows a message on the UI that the user must fill in a Server IP address.
					textView_status.setText("State: " + androidState.getState().toString() + "\nError: no valid Server IP address!");
				}			
			}
			
		});
		
		 // onClick is executed when the user push on the 'Stop testing' button
		button_StopTest.setOnClickListener(new Button.OnClickListener () {
			@Override
			public void onClick(View arg0) {
				if (androidState.getState() instanceof AndroidStateTest) {
					androidState.changeState(AndroidEvents.STOP_TEST, mainHandler);
					// Enable/Disable the UI components
				} else {
					// the test can't be stopped because the Android isn't in the Test state.
				}	
				// Displays the state on the UI.
				textView_status.setText ("State: " + androidState.getState().toString());
				// Enable/Disable the UI components
				setUIComponentsEnabled(androidState.getState().toString());
			}
		});
			
	}	// onCreate
	

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

		// Enable/Disable the UI components
		button_StartExpose.setEnabled((state=="Android Idle" || state=="Android Ready")?true: false);
		button_StopTest.setEnabled((state=="Android Test")?true: false);
		editText_IpAddress.setEnabled((state=="Android Idle" || state=="Android Ready")?true: false);

		// Hide/Shows the UI components
		button_StartExpose.setVisibility((state=="Android Idle" || state=="Android Ready")?View.VISIBLE: View.GONE);
		button_StopTest.setVisibility((state=="Android Test")?View.VISIBLE: View.GONE);
		editText_IpAddress.setVisibility((state=="Android Idle" || state=="Android Ready")?View.VISIBLE: View.GONE);

	} // setUIComponentsEnabled
}
