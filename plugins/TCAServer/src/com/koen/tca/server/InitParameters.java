package com.koen.tca.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InitParameters {

	private static InitParameters self;

	private String initFile;

	// If the serverinit.ini file can't be read a testSet path, then this is the default path.
	private final String DEFAULTTESTSETPATH = "c:/testscript/";

	// if the serverinit.ini file can't be read a results path, than this is the default path.
	private final String DEFAULTRESULTSPATH = "c:/testscript/results/";
		
	// the in-memory parameters from the serverinit.ini file
	private Map<String, String> initParams;
		
	
	private InitParameters () {
		// only accessible by itself
		
		// Initialize the map that holds the parameters from the serverinit.ini file
		this.initParams = new HashMap<String, String>();
	}
	
	public static InitParameters SINGLETON () {
		if (self == null) {
			self = new InitParameters ();
		}
		return self;
	}
	

	/**
	 * Reads the .ini file and store all the parameters in the map.
	 * @param initFile
	 */
	public synchronized boolean initialize (String initFile) {
		
		boolean isFounded = true;
		
		this.initFile = initFile;
		
		// Try reading the serverinit.ini file.
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.initFile));

			String key, value;
			String line;
			
			// loop through all the lines in the serverinit.ini file.
			while ((line = br.readLine()) != null) {
				
				// removes the lines that begins with: //
				if (!line.matches("^//")) {
					
					// any valid line must have a ':' between the key and the value. If not? skip that line.
					if ((line.indexOf(':')) != -1) {
						
						// key is the text before the ':'.
						key = line.substring(0, line.indexOf(':'));
						
						// value is the text after the ':' without whites paces at the beginning of the substring.
						value = (line.substring(line.indexOf(':')+ 1)).replaceAll("^\\s+","");
						
						// saves the key/value pair.
						initParams.put(key, value);
					}
				}
			}
			br.close();
		
			// is the ini file was founded but with no valid parameters, than there is NO ini file. Default parameters are used.
			if (initParams.isEmpty()) {
				isFounded = false;
				this.initFile = null;
			}
		} catch (FileNotFoundException e) {
			// no serverini.ini file, so use default directory
			// so testPath = DEFAULTTESTPATH
			isFounded = false;
			this.initFile = null;
			
		} catch (IOException e) {
			isFounded = false;
			this.initFile = null;
		}

		return isFounded;
	}		

	/**
	 *  return the directory path to the testSets
	 * @return the String testSet path
	 */
	public synchronized String getTestSetPath () {
		// sets the testPath to the parameter from the serverinit.ini file
		if (initParams.get("testsetpath") == null) {
			// there was no testSet path in the .ini file, so we use the default
			initParams.put("testsetpath", DEFAULTTESTSETPATH);
		}
		return initParams.get("testsetpath");
	}

	/**
	 * return the directory path to the results
	 * @return the String result path
	 */
	public synchronized String getResultsPath () {
		if (initParams.get("resultspath") == null) {
			// there was no results path in the .ini file, so we use the default
			initParams.put("resultspath", DEFAULTRESULTSPATH);	
		}
		return initParams.get("resultspath");
	}
}
