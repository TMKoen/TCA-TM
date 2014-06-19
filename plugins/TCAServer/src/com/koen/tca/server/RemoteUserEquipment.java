package com.koen.tca.server;

/**
 * 
 * @version
 * @author Koen
 *
 */
public class RemoteUserEquipment implements Runnable {

	protected int portNumber;			// Server port number
	
	public RemoteUserEquipment (int port) {
		setPortNumber (port);
	}

	public RemoteUserEquipment () {
		//default port number. Java listening on a random port number (greater or equal than 1024)
		portNumber = 0;	
	}
	
	/**
	 * Check if the given port number is valid (between 1025 and 65536) 
	 * and is so, set the port to that number
	 * @param  : port number
	 * @return : true: port is valid and set; false: port is not valid and not set
	 */
	public boolean setPortNumber (int port) {
		boolean isValid = false;
		if (port >= 1024 && port < 65536) {
			this.portNumber = port;
			isValid = true;
		}
		return isValid;
	}
	
	
	public int getPortNumber () {
		return this.portNumber;
	}
	
	/**
	 * Thread runnable method
	 */
	@Override
	public void run() {
		
	}
}
