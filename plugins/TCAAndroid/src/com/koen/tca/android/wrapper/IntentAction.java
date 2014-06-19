package com.koen.tca.android.wrapper;


// Tijdelijke classe. Deze classe moet de classe Action in het Server project zijn!!
public class IntentAction {
	
	private String name;
		
	public IntentAction () {
		name = null;
	}
		
	public void setName (String name) {
			this.name = name;
	}
	
	public String getName () {
		return name;
	}
}
