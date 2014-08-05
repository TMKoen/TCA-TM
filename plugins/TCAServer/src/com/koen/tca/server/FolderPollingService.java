package com.koen.tca.server;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.emf.common.util.URI;

/**
 * Returns a collection of files.
 * @author Christophe
 */
public class FolderPollingService {

	// Specify as a
	private String scriptPath;

	private File[] filesInDirectory;

	public FolderPollingService() {
		// Sets the script path to the default test set.
		scriptPath = "c:\\tcaserver\\testsets\\default\\";
	}

	public void poll() {

		try {

			if (scriptPath == null) {
				return; // Can't poll unknown directory.
			}

			final File rootFile = new File(scriptPath);
			if (rootFile.isDirectory()) {
				// Optionally specify a filter to find DragonX Files.
				filesInDirectory = rootFile.listFiles();
				
/*
				FileFilter filter = new FileFilter () {
					@Override
					public boolean accept (File pathname) {
						return pathname.toLowerCase().endsWith(".xtext");
					}
				}


				filesInDirectory = rootFile.listFiles(new FileNameFilter () {
					public boolean accept (File dir, String name) {
						return name.toLowerCase().endsWith(".xtext");
					}
				});
			}
	*/
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	public URI fileAsURI(File file) {
		URI fileAsURI = URI.createFileURI(file.getAbsolutePath());
		return fileAsURI;

	}

}
