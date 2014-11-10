package com.koen.tca.android;

import java.util.Date;
import java.util.List;

import com.koen.tca.android.DeviceIdentifier;
import com.koen.tca.android.action.PhoneStateListenerMessages;
import com.koen.tca.android.state.AndroidStateIdle;
import com.koen.tca.android.state.AndroidStateMachine;
import com.koen.tca.android.state.AndroidStateReady;
import com.koen.tca.android.state.AndroidStateTest;


import com.koen.tca.android.state.IAndroidState;
import com.koen.tca.common.message.AndroidEvents;
import com.koen.tca.common.message.RemoteCallInfo;
import com.koen.tca.common.message.RemoteNetworkInfo;
import com.koen.tca.common.message.telephony.AndroidCellInfoCdma;
import com.koen.tca.common.message.telephony.AndroidCellInfoGsm;
import com.koen.tca.common.message.telephony.AndroidCellInfoLte;
import com.koen.tca.common.message.telephony.AndroidCellInfoWcdma;
import com.koen.tca.common.message.telephony.AndroidCellLocation;
import com.koen.tca.common.message.telephony.AndroidCellLocationCdma;
import com.koen.tca.common.message.telephony.AndroidCellLocationGsm;
import com.koen.tca.common.message.telephony.AndroidNeighboringCellInfo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
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
@TargetApi(Build.VERSION_CODES.KITKAT)
public class TcaMainActivity extends Activity {

	// Creates an object of ActionRunnner. It is a singleton, so there can be only one object of this class.
	private ActionRunner actionRunner = ActionRunner.SINGLETON();
	
	// A reference to the singleton object which hold the device identification 
	// information like IMEI and telephone number.
	// It also holds the IP address and the port number of the Server to communicate to the Server.
	private DeviceIdentifier deviceInfo = DeviceIdentifier.SINGLETON();
	
	// Holds and manage the state of the Android device: Idle, Expose, Ready or Test.
	private AndroidStateMachine androidState;
	
	// The port number for the socket connection to the Server.
	private final int PORTNUMBER = 8888;
	
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

	// The handler that listens to PhoneState changes like: ringing, off hook, etc.
	private PhoneStateListener listener;
	
	// Handler to the telephony network information (cell info, telephone number, etc).
	private TelephonyManager manager;
	
	// Holds the information when the call is invoked.
	private RemoteCallInfo callInfo;
	
	
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


		
		
		
		// Initialize the state machine. The Android goes to the Idle state by default.
		androidState = new AndroidStateMachine ();
		
		// find the UI objects (buttons. text fields, etc).
		editText_IpAddress = (EditText) findViewById (R.id.EditText_ipaddr);
		editText_phoneNumber = (EditText) findViewById(R.id.editText_telephone_nr);
		button_StartExpose = (Button) findViewById(R.id.button_find_server);
		button_StopTest = (Button) findViewById (R.id.button_stop_testing);
		textView_State = (TextView) findViewById(R.id.textView_State);
		textView_Status = (TextView) findViewById (R.id.textView_Status);
		textView_Imei = (TextView) findViewById(R.id.textView_Imei);
		textView_Number = (TextView) findViewById(R.id.textView_Number);
	
		// Set the context for the actions
		actionRunner.setContext(this);

		// gets the DeviceIdentifier object (singleton) and initialize it with the IMEI 
		// and telephone number of this Android device.
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
				// Check if the message is an event message. Otherwise, do nothing with it.
				if (msg.obj instanceof AndroidEvents) {
					AndroidEvents event = (AndroidEvents) msg.obj;	
				
					// Change the present state, dependent of the parameter:event.
					// It the wrong event is send, the present state is'n changed.
					// So the thread (or the Server) that sends this message,
					// must be aware of the present state.
					androidState.changeState(event, mainHandler);
				
					// Displays the Android device state on the UI.
					textView_State.setText(getStateString());

					// change the UI components to enable or disable, depends of the present state.
					setUIComponentsEnabled(androidState.getState());
				} else
				if (msg.obj instanceof PhoneStateListenerMessages) {
					PhoneStateListenerMessages  phoneStateMessage = (PhoneStateListenerMessages) msg.obj;
					switch (phoneStateMessage) {
					case LISTEN_CALL:
						activateCallListener();
						break;
					case LISTEN_INCOMINGCALL:
						break;
					case LISTEN_GETWEBPAGE:
						break;
					case STOP_LISTENING:
						stopPhoneStateListener();
						break;
					}
					
				}
			} 
		};		


		// Displays the Android device state on the UI.
		textView_State.setText(getStateString());
		
		// display the Android device IMEI and telephone number on the UI.
		textView_Imei.setText(getImeiString ());
		textView_Number.setText (getMsisdnString ());

		// change the UI components to enable or disable, depends of the present Idle state (the default by initializing).
		setUIComponentsEnabled(androidState.getState());
		
		 // Initialize the onClick method that is activated when a user (engineer) push on
		// the UI button: Register Android.
		button_StartExpose.setOnClickListener(new Button.OnClickListener () {
			@Override
			public void onClick(View arg0) {
				
				// Gets the IP address. If it is not valid, null is returned.
				String ipAddr = getValidIpAddress(editText_IpAddress.getText().toString());
				
				// Check if the IP address is valid.
				if (ipAddr != null) {
					
					// Clears the status on the UI.
					textView_Status.setText(getStatusString(""));					

					// Add the Server IP address and the port number to the singleton deviceInfo object.
					deviceInfo.initServerCommunication(ipAddr, PORTNUMBER);

					// Check if the Android device telephone number is found automatically.
					if (deviceInfo.getTelephoneNumber()== null || "".equals(deviceInfo.getTelephoneNumber ())) {
						// Get the telephoneNumber filled in by the user and place it in deviceInfo.
						// If the user don't fill in a (valid) number, then it is null.
						deviceInfo.setTelephoneNumber(getValidMsisdn());

						// Display the telephone number on the UI.
						textView_Number.setText (getMsisdnString ());
					}
			
					// check it a second time, because deviceInfo has already a valid number from
					// the previous if statement.
					if (deviceInfo.getTelephoneNumber() != null && !"".equals(getValidMsisdn())) {
						// go to the Expose state
						androidState.changeState(AndroidEvents.START_EXPOSE, mainHandler);
						
						// Change the UI components to enable or disable, depends of the present state.
						setUIComponentsEnabled(androidState.getState());	
					} else {
						// Shows a message on the UI that the user must fill in a telephone number.
						textView_Status.setText (getStatusString ("Error: no (valid) telephone number!"));
					}
								
				} else {
					// Shows a message on the UI that the user must fill in a Server IP address.
					textView_Status.setText (getStatusString("Error: no valid Server IP address!"));
				}			
				
				// Displays the Android device state on the UI.
				textView_State.setText (getStateString ());
			}
			
		});
		
		 // onClick is executed when the user push on the 'Stop testing' button
		// the button is only visible if the Android is in the test state.
		button_StopTest.setOnClickListener(new Button.OnClickListener () {
			@Override
			public void onClick(View arg0) {
				if (androidState.getState() instanceof AndroidStateTest) {

					// Clears the status on the UI.
					textView_Status.setText (getStatusString (""));
					androidState.changeState(AndroidEvents.STOP_TEST, mainHandler);

					// change the UI components to enable or disable, depends of the present state.
					setUIComponentsEnabled (androidState.getState());
				}
				
				// Displays the Android device state on the UI.
				textView_State.setText(getStateString());
				
				// Enable/Disable the UI components
				setUIComponentsEnabled(androidState.getState());
			}
		});
			
	}	// onCreate
	
	/**
	 * shows the state of the Android device on the UI.
	 * @version
	 * @author Koen Nijmeijer 
	 */
	private String getStateString () {
		return "State: " + androidState.getState().toString();
	}
	
	/**
	 * Shows the status on the UI.
	 * @version
	 * @author Koen Nijmeijer
	 * @param status the status string to be showed
	 */
	private String getStatusString (String status) {
		return "Status: " + status;
	}
	
	/**
	 * Shows the IMEI number on the UI.
	 * @version
	 * @author Koen Nijmeijer
	 */
	private String getImeiString () {
		String imeiString;
		if (deviceInfo.getImeiNumber() != null || !"".equals(deviceInfo.getImeiNumber())) {
			imeiString = "IMEI: " + deviceInfo.getImeiNumber();
		} else {
			imeiString = "This device has no Imei Number!";
		}
		return imeiString;
	}
	
	/**
	 * Shows the telephone number on the UI.
	 * @version
	 * @author Koen Nijmeijer
	 */
	private String getMsisdnString () {
		String msisdn;
		
		if (deviceInfo.getTelephoneNumber() != null && !"".equals(deviceInfo.getTelephoneNumber())) {
			msisdn = "Number: " + deviceInfo.getTelephoneNumber();
		} else {
			msisdn = "No telephone number found!";
		}
		return msisdn;
	}
	
	private String getValidMsisdn () {
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
	private String getValidIpAddress (String ipAddress) {

		// Initialize the return value.
		String validIpAddress = null;
		

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
		
		if (ipAddress.matches(IPPATTERN)) {
			// IP address matches the pattern.
			
			// Splits the IP address in four parts, each part is one octal of the IP address.
			String octals[] = ipAddress.split("\\.");

			if (octals.length == 4) {		
				// it must be, because we checked it earlier with ipAddr.matches(IPPATTERN)
		
				// check if IP address is:
				//			- 10.x.x.x or 010.x.x.x
				//			- 172.16.x.x to 172.31.x.x
				//			- 192.168.x.x
				//			- 127.0.0.1
				// If all this is true, then skip the first '0' if it is there (like 010.x.x.x ==> 10.x.x.x).
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
				)? (ipAddress.charAt(0) == '0')?ipAddress.substring(1):ipAddress :null;
			}
		}
	
		return validIpAddress;
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
	private void setUIComponentsEnabled (IAndroidState state) {

		// Enable/Disable the UI components.
		button_StartExpose.setEnabled((state instanceof AndroidStateIdle || state instanceof AndroidStateReady)?true: false);
		button_StopTest.setEnabled((state instanceof AndroidStateTest)?true: false);
		editText_IpAddress.setEnabled((state instanceof AndroidStateIdle || state instanceof AndroidStateReady)?true: false);
		editText_phoneNumber.setEnabled((state instanceof AndroidStateIdle)?true: false);

		// Hide/Shows the UI components.
		button_StartExpose.setVisibility((state instanceof AndroidStateIdle || state instanceof AndroidStateReady)?View.VISIBLE: View.GONE);
		button_StopTest.setVisibility((state instanceof AndroidStateTest)?View.VISIBLE: View.GONE);
		editText_IpAddress.setVisibility((state instanceof AndroidStateIdle || state instanceof AndroidStateReady)?View.VISIBLE: View.GONE);
		editText_phoneNumber.setVisibility((state instanceof AndroidStateIdle)?View.VISIBLE: View.GONE);
		
	} // setUIComponentsEnabled
	
	/**
	 * This method is called from the ActionRunner in another Thread, so it must be synchronized.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @return
	 */
	public synchronized RemoteNetworkInfo getNetworkInfo() {
		RemoteNetworkInfo info = new RemoteNetworkInfo();
		
		// Gets a telephony manager.
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
			// Fills the RemoteNetworkInfo
			info.setDate(new Date ());
			info.setCallState(manager.getCallState());
			info.setDataActivity(manager.getDataActivity());
			info.setDataState(manager.getDataState());
			info.setDeviceIDID(manager.getDeviceId());
			info.setDeviceSoftwareVersion(manager.getDeviceSoftwareVersion());
//level18			info.setGroepIdLevel1(manager.getGroupIdLevel1());
			info.setHasICCCard(manager.hasIccCard());
			
			// TODO.. check if GSM or CDMA.. deviceID is not always Imei
			info.setImei(manager.getDeviceId());
			info.setLine1Number(manager.getLine1Number());
//level19			info.setMmsUserAgent(manager.getMmsUserAgent());
//level19			info.setMmsUserAgentUrl(manager.getMmsUAProfUrl());
			
			// getNetworkCountryIso is unreliable by CDMA network, so skip that.
			if (!(manager.getPhoneType() == manager.PHONE_TYPE_CDMA)) {
				info.setNetworkCountryISO(manager.getNetworkCountryIso());
				info.setNetworkOperator(manager.getNetworkOperator());
				info.setNetworkOperatorname(manager.getNetworkOperatorName());
			}
			
			info.setNetworkType(manager.getNetworkType());
			info.setPhoneType(manager.getPhoneType());
			info.setRoaming(manager.isNetworkRoaming());
			info.setSIMcountryISO(manager.getSimCountryIso());
			info.setSimOperator(manager.getSimOperator());
			info.setSimOperatorName(manager.getSimOperatorName());
			info.setSimSerialNumber(manager.getSimSerialNumber());
			info.setSimState(manager.getSimState());
			info.setSubscriberId(manager.getSubscriberId());
			info.setVoiceMailAlphaTag(manager.getVoiceMailAlphaTag());
			info.setVoiceMailNumber(manager.getVoiceMailNumber());

			CellLocation cellLocation = manager.getCellLocation();
			
			if (cellLocation instanceof GsmCellLocation) {
				AndroidCellLocationGsm cl = (AndroidCellLocationGsm) new AndroidCellLocationGsm ();
				
				cl.setCid(((GsmCellLocation) cellLocation).getCid());
				cl.setLac(((GsmCellLocation) cellLocation).getLac());
				// AIP level 9
				// cl.setPsc(((GsmCellLocation) cellLocation).getPsc());

				info.setCellLocation(cl);			

			} else if (cellLocation instanceof CdmaCellLocation) {
				AndroidCellLocationCdma cl = new AndroidCellLocationCdma ();
				
				cl.setBaseStationId(((CdmaCellLocation) cellLocation).getBaseStationId());
				cl.setBaseStationLatitude(((CdmaCellLocation) cellLocation).getBaseStationLatitude());
				cl.setBaseStationLongitude(((CdmaCellLocation) cellLocation).getBaseStationLongitude());
				cl.setNetworkId(((CdmaCellLocation) cellLocation).getNetworkId());
				cl.setSystemId(((CdmaCellLocation) cellLocation).getSystemId());

				info.setCellLocation(cl);							
			}

			// Read all the cell info's in the device. it can be an CellInfoGSM, CellInfoCdma, CellInfoLTE or CellInfoWcdma
			List<CellInfo> cellInfoList = manager.getAllCellInfo();
			if (cellInfoList != null) {
				// Loop through all the CellInfo's
				for (CellInfo cellInfo : cellInfoList) {
					if (cellInfo instanceof CellInfoGsm) {
						//GSM Cell Info
						AndroidCellInfoGsm c = new AndroidCellInfoGsm ();
						
						// Fill in the AndroidCellInfoGsm class
						c.setTimeStamp(cellInfo.getTimeStamp());
						c.setIsRegisterd(cellInfo.isRegistered());
						c.setCid(((CellInfoGsm) cellInfo).getCellIdentity().getCid());
						c.setLac(((CellInfoGsm) cellInfo).getCellIdentity().getLac());
						c.setMcc(((CellInfoGsm) cellInfo).getCellIdentity().getMcc());
						c.setMnc(((CellInfoGsm) cellInfo).getCellIdentity().getMnc());
						
						// PSC is deprecated by Java Android and undefined by GSM
	//					c.setPsc(((CellInfoGsm) cellInfo).getCellIdentity().getPsc());
	
						c.setAsuLevel(((CellInfoGsm) cellInfo).getCellSignalStrength().getAsuLevel());
						c.setDmb(((CellInfoGsm) cellInfo).getCellSignalStrength().getDbm());
						c.setSignalLevel(((CellInfoGsm) cellInfo).getCellSignalStrength().getLevel());
	
						// Add the AndroidCellInfoGsm to the CellInfo list in RemoteNetworkInfo.
						info.addCellInfo(c);
						
						
					} else if (cellInfo instanceof CellInfoCdma) {
						// Cdma Cell Info				
						AndroidCellInfoCdma c = new AndroidCellInfoCdma ();
						
						// Fill in the AndroidCellInfoCdma class
						c.setTimeStamp(cellInfo.getTimeStamp());
						c.setIsRegisterd(cellInfo.isRegistered());
						c.setBaseStationId(((CellInfoCdma) cellInfo).getCellIdentity().getBasestationId());
						c.setLatitude(((CellInfoCdma) cellInfo).getCellIdentity().getLatitude());
						c.setLongitude(((CellInfoCdma) cellInfo).getCellIdentity().getLongitude());
						c.setNetworkId(((CellInfoCdma) cellInfo).getCellIdentity().getNetworkId());
						c.setSystemId(((CellInfoCdma) cellInfo).getCellIdentity().getSystemId());
						c.setAsuLevel(((CellInfoCdma) cellInfo).getCellSignalStrength().getAsuLevel());
						c.setCdmaDbm(((CellInfoCdma) cellInfo).getCellSignalStrength().getCdmaDbm());
						c.setCdmaEcio(((CellInfoCdma) cellInfo).getCellSignalStrength().getCdmaEcio());
						c.setCdmaLevel(((CellInfoCdma) cellInfo).getCellSignalStrength().getCdmaLevel());
						c.setDbm(((CellInfoCdma) cellInfo).getCellSignalStrength().getDbm());
						c.setEvdoDbm(((CellInfoCdma) cellInfo).getCellSignalStrength().getEvdoDbm());
						c.setEvdoEcio(((CellInfoCdma) cellInfo).getCellSignalStrength().getEvdoEcio());
						c.setEvdoLevel(((CellInfoCdma) cellInfo).getCellSignalStrength().getEvdoLevel());
						c.setEvdoSnr(((CellInfoCdma) cellInfo).getCellSignalStrength().getEvdoSnr());
						c.setSignalLevel(((CellInfoCdma) cellInfo).getCellSignalStrength().getLevel());
						
						// Add the AndroidCellInfoCdma to the CellInfo list in RemoteNetworkInfo.
						info.addCellInfo(c);
						
					} else if (cellInfo instanceof CellInfoLte) {
						// LTE cell info
						AndroidCellInfoLte c = new AndroidCellInfoLte ();
	
						// Fill in the AndroidCellInfoLte class
						c.setTimeStamp(cellInfo.getTimeStamp());
						c.setIsRegisterd(cellInfo.isRegistered());
						c.setCi(((CellInfoLte) cellInfo).getCellIdentity().getCi());
						c.setMcc(((CellInfoLte) cellInfo).getCellIdentity().getMcc());
						c.setMnc(((CellInfoLte) cellInfo).getCellIdentity().getMnc());
						c.setPci(((CellInfoLte) cellInfo).getCellIdentity().getPci());
						c.setTac(((CellInfoLte) cellInfo).getCellIdentity().getTac());
						c.setAsuLevel(((CellInfoLte) cellInfo).getCellSignalStrength().getAsuLevel());
						c.setDmb(((CellInfoLte) cellInfo).getCellSignalStrength().getDbm());
						c.setSignalLevel(((CellInfoLte) cellInfo).getCellSignalStrength().getLevel());
						c.setTimingAdvance(((CellInfoLte) cellInfo).getCellSignalStrength().getTimingAdvance());
						
						// Add the AndroidCellInfoLte to the CellInfo list in RemoteNetworkInfo
						info.addCellInfo(c);
						
					} else if (cellInfo instanceof CellInfoWcdma) {
						// Wcdma cell info
						AndroidCellInfoWcdma c = new AndroidCellInfoWcdma ();
						
						c.setTimeStamp(cellInfo.getTimeStamp());
						c.setIsRegisterd(cellInfo.isRegistered());
						c.setCid(((CellInfoWcdma) cellInfo).getCellIdentity().getCid());
						c.setLac(((CellInfoWcdma) cellInfo).getCellIdentity().getLac());
						c.setMcc(((CellInfoWcdma) cellInfo).getCellIdentity().getMcc());
						c.setMnc(((CellInfoWcdma) cellInfo).getCellIdentity().getMnc());
						c.setPsc(((CellInfoWcdma) cellInfo).getCellIdentity().getPsc());
						c.setAsuLevel(((CellInfoWcdma) cellInfo).getCellSignalStrength().getAsuLevel());
						c.setDmb(((CellInfoWcdma) cellInfo).getCellSignalStrength().getDbm());
						c.setSignalLevel(((CellInfoWcdma) cellInfo).getCellSignalStrength().getLevel());
						
						// Add the AndroidCellInfoWcdma to the CellInfo list in RemoteNetworkInfo
						info.addCellInfo(c);
					}
				} // for loop
			} // if
			
			// Read all the Neighboring cell info's in the device. it can be an CellInfoGSM, CellInfoCdma, CellInfoLTE or CellInfoWcdma
			List<NeighboringCellInfo> neighboringCellInfoList = manager.getNeighboringCellInfo();

			for (NeighboringCellInfo cellInfo: neighboringCellInfoList) {
				AndroidNeighboringCellInfo c = new AndroidNeighboringCellInfo ();
				
				c.setCid(cellInfo.getCid());
				c.setLac(cellInfo.getLac());
				c.setNetworkType(cellInfo.getNetworkType());
				c.setPsc(cellInfo.getPsc());
				c.setRssi(cellInfo.getRssi());
				
				// add the AndroidNeighboringCellInfo to the Neighboring CellInfo list in RemoteNetworkInfo
				info.addNeighboringCellInfo(c);
			}
			
	
		return info;
	}
	
	private void activateCallListener () {
		
	
		
		// Initialize the object that holds the information when the call action is invoked.
		callInfo = new RemoteCallInfo ();
		
		// Sets the present date and time to the starting call date/time.
		callInfo.setCallDate(new Date());
		
		// sets the Idle date and time to the present date/time.
		callInfo.setIdleDate(new Date());
		
		manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		listener = new PhoneStateListener () {
			
			public void onCallStateChanged (int state, String incomingNumber) {

				// Pointer to the present date and time.
				Date presentDate;
		
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					// the present date /time.
					presentDate = new Date ();

					if (callInfo.getTimeout() <= (presentDate.getTime() - callInfo.getCallDate().getTime())) {
						// a timeout was occurred
						callInfo.isEndedByTimeout(true);
					}
					
					// The phone was ended OR the phone was ended by a timeout.
					callInfo.isEnded(true);
					
					if (callInfo.getIsOffHooked() == true) {
						// The phone was answered and then ended
						
						// The time in seconds from the offHook status until Idle status.
						callInfo.setOffHookTime((int) (presentDate.getTime() - callInfo.getOffHookDate().getTime() / 1000));			
					} else {
						if (callInfo.getIsRinged() == true) {
							callInfo.setRingedTime((int) (presentDate.getTime () - callInfo.getRingingDate().getTime() / 1000));
						} else {
							// The phone call was not ringed and not answered (off hook).
						}
					}
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					// waiting for a connection.
					presentDate = new Date ();
					
					callInfo.IsRinged(true);
					callInfo.setRingingDate(presentDate);
					callInfo.setIdleTime((int) (presentDate.getTime() - callInfo.getIdleDate().getTime() / 1000));
					
					// Sets the phone number of the called UE.
					callInfo.setCalledMsisdn(incomingNumber);
					
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					presentDate = new Date ();

					// The called phone has answered the call.
					callInfo.isOffHooked(true);
					callInfo.setOffHookDate(presentDate);
					
					if (callInfo.getIsRinged() == true) {
						callInfo.setRingedTime((int) (presentDate.getTime() - callInfo.getRingedTime() / 1000));						
					}
					
					break;
				}
			}
		};
		// Activates the Phone listener.
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);	

	}
	
	private void stopPhoneStateListener () {
		// Stops listening to changed call states.
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);

		// sets the callInfo data in the results
		actionRunner.getResults().setActionInfo(callInfo);
	}
}
