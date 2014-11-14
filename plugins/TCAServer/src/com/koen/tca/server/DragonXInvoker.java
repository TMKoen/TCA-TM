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
import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.DragonX;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.dragonX.TestCase;
import com.netxforge.netxtest.dragonX.UE;
import com.netxforge.netxtest.dragonX.UEMetaObject;
import com.netxforge.netxtest.dragonX.UEPARAMS;
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

	public static String PRESENT_SCRIPTNAME = null;
	
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

							// For testing the script:
							
							for (TestCase t : ((DragonX) script).getTests()) {
								System.out.println("\ntestcase: " + t.getName());
								System.out.println("     Description: " + t.getDescription());

								System.out.println("     Actions:");
								System.out.println("     ========");
								for (Action a : t.getProcedure().getActions()) {
									System.out.println("          ActionCode: " + a.getActionCode().toString());
									System.out.println("          Parameters:");
									System.out.println("          ===========");
									for (Parameter p: a.getParameterSet()) {
										System.out.print("               Parameter: " + p.getName() + ": ");
										if (p.getType() != null) {

											if ((p.getName().toString().equals("From") || p.getName().toString().equals("To") | p.getName().toString().equals("Source")) 
													&& p.getType().getUeRef() != null) {
												
												for (UEMetaObject ue: p.getType().getUeRef().getMeta()) {
													if (ue.getParams() == UEPARAMS.MSISDN) {
														System.out.println(ue.getParamValue() );														
													}
												}											
											} else if (p.getType().getValue() != 0) {
												System.out.println(p.getType().getValue());
											} else if (p.getName().toString().equals("Response") && p.getType().getResponse() != null) {
												System.out.println(p.getType().getResponse().toString());
											} else if (p.getName().toString().equals("Direction") && p.getType().getSmsDirection() != null) {
												System.out.println(p.getType().getSmsDirection().toString());
												
											} else if ((p.getName().toString().equals("URL") || p.getName().toString().equals("Data")) && p.getType().getStringValue() != null) {
												System.out.println(p.getType().getStringValue());
											} else if (p.getName().toString().equals("DataAction") && p.getType().getDataAction() != null) {
												System.out.println(p.getType().getDataAction().toString());
											}

										}

									}
								}								
							}
							
							for (UE ue: ((DragonX) script).getUes()) {
								System.out.println("\nUE: " + ue.getName());
								for (UEMetaObject m : ue.getMeta()) {
										System.out.println("     Description: " + m.getDescription());
									if (m.getParams() != null) {
										if (m.getParams() == UEPARAMS.IMEI) {
											System.out.println("     IMEI: " + m.getParamValue());
										} else if (m.getParams() == UEPARAMS.MSISDN) {
											System.out.println("     MSISDN: " + m.getParamValue());
										}
									}
								}
							}
							// End of testing the script.
							
							String name = filesInDirectory[testsDone].getName();
							
							// store the present script name without '.dragonx'
							setPresentScript ((name.lastIndexOf(".") != -1) ? name.substring(0, name.lastIndexOf(".")): name);

							// evaluate the script and execute it.
							interpreter.evaluate((DragonX) script);

							// sleep until all the actions from this script are finished.
							// After that, the next script can begin.
							while (AndroidDispatcher.NO_JOBS_BUSY != true) {
								// sleep one second
								Thread.sleep(1000);
							}
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

	
	private synchronized void setPresentScript (String name) {
		PRESENT_SCRIPTNAME = name;
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
