package com.koen.tca.server;

import java.io.File;

/**
 * Returns a collection of files.
 * 
 */
public class FolderPollingService {

	// Specify as a
	private String scriptPath;

	private File[] filesInDirectory;

	public FolderPollingService() {
	}

	public void poll() {

		if(scriptPath == null){
			return; // Can poll unknown directory. 
		}
		
		final File rootFile = new File(scriptPath);
		if (rootFile.isDirectory()) {

			// Optionally specify a filter to find DragonX Files.
			filesInDirectory = rootFile.listFiles();
		}
	}

	public File[] getFilesInDirectory() {
		return filesInDirectory;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
	
	
	
}
