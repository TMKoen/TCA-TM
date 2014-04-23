package com.tmobile.tca.Actions;

import java.util.Currency;

import com.tmobile.tca.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Time;

public class Call extends MobileAction{

	// Data members
	String telephoneNumber;
	Time callTime;				// The length of the call in minutes/seconds.
	Time timeOfset;				// The time to wait before the action starts.
	
	
	// Constructors
	public Call ()
	{

	}
	
	// Methods
	
	public void StartAction (MainActivity m)
	{
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ telephoneNumber));
		m.startActivity (callIntent);
	}
}
