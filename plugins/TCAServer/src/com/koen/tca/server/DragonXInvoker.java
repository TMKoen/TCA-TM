package com.koen.tca.server;

import java.io.File;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.google.inject.Inject;
import com.netxforge.netxtest.dragonX.DragonX;
import com.netxforge.netxtest.interpreter.DragonXInterpreter;
import com.netxforge.netxtest.interpreter.IExternalDispatcher;

/**
 * A service for calling the Dragon X Interpreter. Injects a Factory for
 * producing XTextResources.
 * 
 * @version
 * @author Koen Nijmeijer
 */
public class DragonXInvoker implements Runnable {

	@Inject
	private IResourceFactory xResourceFactory;

	@Inject
	private FolderPollingService scriptObtainer;

	@Inject
	private DragonXInterpreter interpreter;

	@Inject
	private IExternalDispatcher androidDispatcher;

	// Handler to the Thread and the name of the thread.
	private Thread invokerThread;

	// The name of our Thread.
	private final String threadName = "InvokerThread";

	// If this bit is true, than the thread must be stopped.
	private boolean stopThread;
	
	private File [] filesInDirectory;
	
	// the testSet that is tested.
	private String testSet = null;
	
	private int totalTestScripts = 0, testsDone = 0;
	
	public DragonXInvoker () {
		invokerThread = null;
	}
	
	
	protected XtextResource doGetResource(URI uri) throws Exception {

		XtextResource resource = null;

		if (resource == null) {
			resource = (XtextResource) xResourceFactory.createResource(uri);
		}

		resource.load(null);

		if (resource instanceof LazyLinkingResource) {
			// Linking process here.
			((LazyLinkingResource) resource)
					.resolveLazyCrossReferences(CancelIndicator.NullImpl);
		} else {
			EcoreUtil.resolveAll(resource);
		}

		return resource;
	}

	/**
	 * Invokes a {@link DragonX} script by scanning a directory for script
	 * files. Parse it and produce an AST. Then configure an
	 * {@link DragonXInterpreter} and finally invoke the interpreter with the
	 * AST. This will emmit actions on the specified {@link IExternalDispatcher}
	 */
	public void invoke(String testSet, String [] testCases) {

		this.testSet = testSet;
		// Setup the interpreter.
		interpreter.setExtDispatcher(androidDispatcher);

		try {

			// search all the dragonX files in the directory.
			// testSet is the name of the testSet. That is a Directory in the main path.
			// testCases is a String array with all the testcase names in the testSet that must be tested.
			filesInDirectory = scriptObtainer.poll(testSet, testCases);

			if (filesInDirectory != null && filesInDirectory.length > 0) {
				
				// activate a new thread that loops through all the testcases. 
					startThread();
/*		
				URI fileAsURI = scriptObtainer.fileAsURI(filesInDirectory[0]);

				XtextResource scriptAsResource = this.doGetResource(fileAsURI);
				if (!scriptAsResource.getContents().isEmpty()) {
					EList<EObject> contents = scriptAsResource.getContents();
					if (contents.size() == 1) {
						EObject eObject = contents.get(0);
						if (eObject instanceof DragonX) {
							DragonX script = (DragonX) eObject;

							// Setup the interpreter.
							interpreter.setExtDispatcher(androidDispatcher);
							interpreter.evaluate(script);
						} else {
							// Invalid EMF Object.
						}
					}

				} else {
					// invalid script, throw Exception
				}
*/
			} else {
				// No files throw Exception
			}

		} catch (Exception e) {
			e.printStackTrace();
			// catch any other exception
		}
	}

	@Override
	public void run() {

		URI fileAsURI;
		XtextResource scriptAsResource;
		
		// Number of all the testcases
		totalTestScripts = filesInDirectory.length;
		testsDone = 0;

		// Stop the while loop when TestServer.stopTest(..) is called or testing all the scripts are done.
		while (stopThread != true && testsDone < totalTestScripts) {
			
			// Gets the URI of the testcase file
			fileAsURI = scriptObtainer.fileAsURI(filesInDirectory[testsDone]);
			try {
				scriptAsResource = this.doGetResource(fileAsURI);

				// testCase is not empty
				if (!scriptAsResource.getContents().isEmpty()) {

					EList<EObject> contents = scriptAsResource.getContents();
					if (contents.size() == 1) {
						EObject script = contents.get(0);

						if (script instanceof DragonX) {
							
							interpreter.evaluate((DragonX) script);
						} else {
							// Invalid EMF Object.
						}
					}

				} else {
					// invalid script, throw Exception
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}	

			// next script
			testsDone++;
		} // while loop
		
	}

	public synchronized void startThread () {

		// if there is another thread running, don't create a new thread
		if (invokerThread == null) {
			invokerThread = new Thread(this, threadName);
			
			// The while loop in the run method can begin.
			stopThread = false;
			invokerThread.start();
		}
		
	}
	
	public synchronized void stopThread () {
		// Stops The run() method (the while loop in it).
		stopThread = true;
	}
	
	@Override
	public String toString() {
		String details = "Total number of scripts: " + totalTestScripts + "\n";
		details += "Scripts done:" + testsDone + "\n\n";  
		details += "TestSet: " + this.testSet + "\n";
		
		details += "\nTestscripts: \n";
		
		for (int i=0; i< totalTestScripts; i++) {
			details += filesInDirectory[i].getName() + "\n";
		}
		return details;
	}
}
