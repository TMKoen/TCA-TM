package com.koen.tca.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;

/**
 * Returns a collection of files.
 * @author Koen Nijmeijer
 */
public class FolderPollingService {

	public FolderPollingService() {
	}

	/**
	 * Finds all the .dragonx testcases, or finds a selection of .dragonx testcases.
	 * @version 1.0
	 * @author Koen Nijmeijer
	 * @param path the String path to the directory where the scripts are stored.
	 * @param testCases a array of Strings that represents a selection of testcases to poll (without '.dragonx')
	 * @return all the .dragonx testcases in the directory of in the array list.
	 */
	public File [] poll (String path, String[] testCases) {
		
		File [] filesInDir = null;
		File rootFile = new File (path);
		
		if (rootFile.isDirectory()) {
			// Set a File filter to find only files with '.dragonx' extensions
			FilenameFilter filter = new FilenameFilter () {
				@Override
				public boolean accept (File dir, String name) {
					// Filters all the .dragonX files
					if (name.substring(name.lastIndexOf('.')).toLowerCase().equals(".dragonx"))
						return true;
					return false;
				}
			};
			
			// Get all the .dragonX script files.
			filesInDir = rootFile.listFiles(filter);
			
			if (testCases != null) {
				// Find all the selected scripts
				List<File> foundedFiles = new ArrayList<File>();
				
				for (String s: testCases) {
					for (File f: filesInDir) {
						if (f.getName().toLowerCase().equals(s.toLowerCase()+ ".dragonx")) {
							// found one
							foundedFiles.add(f);
							break;
						}
					}
				}
				// Change List to array list.
				filesInDir = foundedFiles.toArray(new File[foundedFiles.size()]);
			}
		}
		
		// ==null if there where no Files or invalid path.
		return filesInDir;
	}
	
	public URI fileAsURI(File file) {
		URI fileAsURI = URI.createFileURI(file.getAbsolutePath());
		return fileAsURI;

	}

}
